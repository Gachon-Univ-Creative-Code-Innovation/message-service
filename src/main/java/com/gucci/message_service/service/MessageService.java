package com.gucci.message_service.service;

import com.gucci.message_service.domain.Message;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageRoomResponseDTO;
import com.gucci.message_service.dto.MessageSendRequestDTO;
import com.gucci.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    // 메시지 전송
    public void send(Long senderId, MessageSendRequestDTO request) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .build();

        messageRepository.save(message);
    }

    // 방 리스트 조회
    public List<MessageRoomResponseDTO> getRooms(Long userId) {
        // 로그인 한 유저의 삭제되지 않은 메시지 전체 조회
        List<Message> allMessages = messageRepository.findAllRelatedMessages(userId);

        Map<Long, MessageRoomResponseDTO> rooms = new LinkedHashMap<>();

        for (Message message : allMessages) {
            Long targetId = message.getSenderId().equals(userId)
                    ? message.getReceiverId()
                    : message.getSenderId();

            // 이미 맵에 등록됐으면 스킵
            if(rooms.containsKey(targetId)) continue;

            rooms.put(targetId, MessageRoomResponseDTO.builder()
                    .targetUserId(targetId)
                    .targetNickname("닉네임") // JWT에서 닉네임 가져오기
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getCreatedAt())
                    .build());
        }

        return new ArrayList<>(rooms.values());
    }

    public List<MessageResponseDTO> getMessagesWithTarget(Long userId, Long targetUserId) {
        List<Message> messages = messageRepository.findConversation(userId, targetUserId);
        return messages.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private MessageResponseDTO convertToDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .receiverId(message.getReceiverId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
