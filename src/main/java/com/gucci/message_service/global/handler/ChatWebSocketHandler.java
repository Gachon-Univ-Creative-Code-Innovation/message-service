package com.gucci.message_service.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gucci.message_service.auth.JwtProvider;
import com.gucci.message_service.domain.Message;
import com.gucci.message_service.dto.WebSocketMessageRequestDTO;
import com.gucci.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹 소켓 연결 되었습니다. 세션 ID : {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketMessageRequestDTO request = objectMapper.readValue(message.getPayload(), WebSocketMessageRequestDTO.class);
        if ("AUTH".equals(request.getType())) {
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

            return;
        }


        Long senderId = getUserId(session);
        if (senderId == null) {
            log.warn("인증되지 않은 사용자가 메시지를 보냈습니다.");
            return;
        }



        WebSocketSession receiverSession = userSessions.get(request.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            Message savedMessage = messageRepository.save(
                    Message.builder()
                            .senderId(senderId)
                            .receiverId(request.getReceiverId())
                            .messageType(request.getMessageType())
                            .content(request.getContent())
                            .build()
            );

            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(savedMessage)));
            log.info("메시지를 보냈습니다. message: {}, type: {}", savedMessage.getContent(), savedMessage.getMessageType());
        } else {
            log.warn("연결되지 않거나 없는 사용자에게 메시지를 보냈습니다.");

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
