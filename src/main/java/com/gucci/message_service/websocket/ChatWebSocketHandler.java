package com.gucci.message_service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gucci.message_service.auth.JwtProvider;
import com.gucci.message_service.domain.Message;
import com.gucci.message_service.dto.WebSocketMessageRequestDTO;
import com.gucci.message_service.dto.WebSocketMessageResponseDTO;
import com.gucci.message_service.dto.WebSocketReadEventDTO;
import com.gucci.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<Long, Long> userInRoom = new ConcurrentHashMap<>();

    @Override

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹 소켓 연결 되었습니다. 세션 ID : {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketMessageRequestDTO request = objectMapper.readValue(message.getPayload(), WebSocketMessageRequestDTO.class);
        if ("AUTH".equals(request.getType())) {
            handleAuth(session, request);
            return;
        }

        Long userId = getUserId(session);
        if (userId == null) {
            log.warn("인증되지 않은 사용자가 메시지를 보냈습니다.");
            return;
        }

        switch (request.getType()) {
            case "ENTER":
                handleEnterRoom(userId,request.getRoomId());
                break;
            case "LEAVE":
                handleLeaveRoom(userId);
                break;
            case "MESSAGE":
                handleChatMessage(request, userId);
                break;
            case "READ":
                handleReadMessage(request, userId);
                break;
            default:
                log.warn("알 수 없는 메시지 타입입니다: {}", request.getType());
        }

    }


    private void handleEnterRoom(Long userId, Long roomId) {
        userInRoom.put(userId, roomId);
        log.info("유저 {} 채팅방 {} 입장", userId, roomId);

        Long targetUserId = roomId;
        WebSocketSession targetSession = userSessions.get(targetUserId);

        if (targetSession != null && targetSession.isOpen()) {
            try {
                Map<String, Object> payload = Map.of(
                        "type", "ENTER",
                        "userId", userId,
                        "roomId", roomId
                );
                String json = objectMapper.writeValueAsString(payload);
                targetSession.sendMessage(new TextMessage(json));
                log.info("상대방 {} 에게 입장 알림 전송 완료", targetUserId);
            } catch (IOException e) {
                log.error("상대방에게 입장 알림 전송 실패", e);
            }
        }


    }

    private void handleLeaveRoom(Long userId) {
        userInRoom.remove(userId);
        log.info("유저 {} 채팅방 퇴장", userId);
    }

    private void handleReadMessage(WebSocketMessageRequestDTO request, Long userId) throws Exception {
        Long messageId = request.getMessageId();
        Message message = messageRepository.findById(messageId).orElse(null);

        if (message == null || !message.getReceiverId().equals(userId)) {
            log.warn("읽을 수 없는 메시지입니다. messageId: {}, receiverId: {}", messageId, userId);
            return;
        }

        Long myRoomId = userInRoom.get(userId);

        if (myRoomId == null || !myRoomId.equals(request.getRoomId())) {
            log.info("내가 현재 채팅방에 없으므로 읽음 알림 전송 생략. userId = {}", userId);
            return;
        }

        if (!message.isRead()) {
            message.markAsRead();
            messageRepository.save(message);
            log.info("메시지 읽음 처리 완료. messageId = {}", messageId);
        }

        WebSocketSession senderSession = userSessions.get(message.getSenderId());
        if (senderSession != null && senderSession.isOpen()) {
            WebSocketReadEventDTO readEventDTO = WebSocketReadEventDTO.builder()
                    .messageId(messageId)
                    .readerId(userId)
                    .build();

            senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(readEventDTO)));
            log.info("읽음 알림 전송 완료. to userId: {}", message.getSenderId());
        }
    }

    private void handleChatMessage(WebSocketMessageRequestDTO request, Long userId) throws IOException {
        WebSocketSession receiverSession = userSessions.get(request.getReceiverId());

        Message savedMessage = messageRepository.save(
                Message.builder()
                        .senderId(userId)
                        .receiverId(request.getReceiverId())
                        .messageType(request.getMessageType())
                        .content(request.getContent())
                        .build()
        );

        // 이 채팅방 안 읽은 메시지 수
        long unreadCount = messageRepository.countBySenderIdAndReceiverIdAndIsReadFalse(userId, request.getReceiverId());

        // 전체 채팅방 안 읽은 메시지 수
        long totalUnreadCount = messageRepository.countByReceiverIdAndIsReadFalse(request.getReceiverId());

        WebSocketMessageResponseDTO responseDTO = WebSocketMessageResponseDTO.builder()
                .id(savedMessage.getId())
                .senderId(savedMessage.getSenderId())
                .receiverId(savedMessage.getReceiverId())
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .isRead(savedMessage.isRead())
                .createdAt(savedMessage.getCreatedAt())
                .unreadCount(unreadCount)
                .totalUnreadCount(totalUnreadCount)
                .build();


        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseDTO)));
            log.info("메시지를 보냈습니다. message: {}, type: {}", savedMessage.getContent(), savedMessage.getMessageType());
        } else {
            log.warn("연결되지 않거나 없는 사용자에게 메시지를 보냈습니다.");

        }
    }

    private void handleAuth(WebSocketSession session, WebSocketMessageRequestDTO request) throws IOException {
        if (jwtProvider.validateToken(request.getToken())) {
            Long userId = jwtProvider.getUserId(request.getToken());
            String nickname = jwtProvider.getNickname(request.getToken());
            session.getAttributes().put("userId", userId);
            session.getAttributes().put("nickname", nickname);
            userSessions.put(userId, session);
            log.info("WebSocket 인증 성공 - UserId : {}, NickName : {}", userId, nickname);
        } else {
            session.close(CloseStatus.POLICY_VIOLATION);
            log.warn("WebSocket 인증 실패 - 유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("웹소켓 연결이 종료 되었습니다. userId = {}", userId);
        }
    }

    private Long getUserId(WebSocketSession session) {
        try {
            return (Long) session.getAttributes().get("userId");
        } catch (Exception e) {
            return null;
        }
    }
}
