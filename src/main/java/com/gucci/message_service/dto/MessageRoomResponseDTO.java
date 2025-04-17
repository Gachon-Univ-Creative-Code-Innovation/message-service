package com.gucci.message_service.dto;

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
    private LocalDateTime lastMessageTime;
}
