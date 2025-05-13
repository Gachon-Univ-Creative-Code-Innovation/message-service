package com.gucci.message_service.dto;

import com.gucci.message_service.domain.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebSocketMessageRequestDTO {
    private String type; // AUTH or MESSAGE or ENTER or LEAVE
    private String token; // type이 AUTH일 때 사용
    private Long messageId;
    private Long roomId; // 채팅방 입장 시 사용
    private Long receiverId;
    private String content;
    private MessageType messageType;
}
