package com.gucci.message_service.dto;

import com.gucci.message_service.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MessageResponseDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType messageType;
    private boolean isRead;
    private LocalDateTime createdAt;
}
