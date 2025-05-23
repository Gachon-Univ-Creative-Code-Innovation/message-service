package com.gucci.message_service.dto;

import com.gucci.message_service.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MessageRoomResponseDTO {
    private Long targetUserId; // 상대방 ID
    private String targetNickname; // 상대방 이름
    private String lastMessage;
    private MessageType lastMessageType;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
}
