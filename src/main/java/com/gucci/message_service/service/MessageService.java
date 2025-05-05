package com.gucci.message_service.service;

import com.gucci.common.exception.CustomException;
import com.gucci.common.exception.ErrorCode;
import com.gucci.message_service.client.UserClient;
import com.gucci.message_service.domain.Message;
import com.gucci.message_service.domain.MessageType;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageRoomResponseDTO;
import com.gucci.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final S3Service s3Service;

    // 방 리스트 조회
    public List<MessageRoomResponseDTO> getRooms(Long userId) {
        // 로그인 한 유저의 삭제되지 않은 메시지 전체 조회
        List<Message> allMessages = messageRepository.findAllRelatedMessages(userId);

        // 내 전체 메시지에 존재하는 상대방의 ID 전부 추출
        Set<Long> targetIds = allMessages.stream()
                .map(m -> m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId())
                .collect(Collectors.toSet());

        // 모든 안 읽은 메시지 카운트
        Map<Long, Long> unreadCounts = countUnreadMessages(userId, targetIds);

        // 닉네임 리스트로 가져오기 (FeignClient)
        Map<Long, String> nicknameMap = userClient.getNicknamesByIds(new ArrayList<>(targetIds)).getData();

        // 각 방 별로 최신 메시지만 담기
        Map<Long, MessageRoomResponseDTO> rooms = new LinkedHashMap<>();

        for (Message message : allMessages) {
            Long targetId = message.getSenderId().equals(userId)
                    ? message.getReceiverId()
                    : message.getSenderId();

            // 이미 추가되어있는 사용자면 건너 뜀
            if (rooms.containsKey(targetId)) {
                continue;
            }

            rooms.put(targetId, MessageRoomResponseDTO.builder()
                    .targetUserId(targetId)
                    .targetNickname(nicknameMap.getOrDefault(targetId,"알 수 없음"))
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getCreatedAt())
                    .unreadCount(unreadCounts.getOrDefault(targetId, 0L))
                    .build());
        }

        return new ArrayList<>(rooms.values());
    }

    private Map<Long, Long> countUnreadMessages(Long userId, Set<Long> targetIds) {
        List<Object[]> unreadCountsList = messageRepository.countUnreadMessages(userId, targetIds);
        return unreadCountsList.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // senderId
                        row -> (Long) row[1]  // count
                ));
    }

    // 특정 유저(방)의 전체 메시지 조회
    @Transactional
    public Page<MessageResponseDTO> getMessagesWithTarget(Long userId, Long targetUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));


        // 방 입장 시 전체 읽음 처리
        messageRepository.markAllMessagesAsRead(userId, targetUserId);

        // 최신 순으로 전체 메시지 조회
        Page<Message> messages = messageRepository.findConversation(userId, targetUserId, pageable);

        return messages.map(this::convertToDTO);
    }

    // 특정 유저와의 대화 전체 삭제
    @Transactional
    public void exitRoomWithTarget(Long userId, Long targetUserId) {
        // 받은 & 보낸 메시지 소프트 삭제
        messageRepository.softDeleteReceivedMessage(userId, targetUserId);
        messageRepository.softDeleteSentMessages(userId, targetUserId);

        // 읽음 처리
        messageRepository.markAllMessagesAsRead(userId, targetUserId);

        // 두 사용자에게 삭제된 메시지 DB에서 삭제
        List<Message> conversationAll = messageRepository.findConversationAll(userId, targetUserId);
        List<Message> toDelete = new ArrayList<>();

        for (Message message : conversationAll) {
            if (message.isDeletedBySender() && message.isDeletedByReceiver()) {
                // 이미지 파일인 경우 s3에서 삭제
                if (message.getMessageType() == MessageType.IMAGE) {
                    s3Service.deleteFile(message.getContent());
                }

                toDelete.add(message);
            }
        }

        messageRepository.deleteAll(toDelete);
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (message.getSenderId().equals(userId)) {
            message.markAsDeleteBySender();
        } else if (message.getReceiverId().equals(userId)) {
            message.markAsDeleteByReceiver();
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 두 사용자에게서 삭제 되었으면 실제 DB에서 삭제
        if (message.isDeletedBySender() && message.isDeletedByReceiver()) {
            if (message.getMessageType() == MessageType.IMAGE) {
                s3Service.deleteFile(message.getContent());
            }

            messageRepository.delete(message);
        }
    }

    public long getAllUnreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalseAndDeletedByReceiverFalse(userId);
    }

    private MessageResponseDTO convertToDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .receiverId(message.getReceiverId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
