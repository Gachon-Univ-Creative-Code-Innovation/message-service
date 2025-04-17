package com.gucci.message_service.dto;

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
    private boolean isRead;
    private LocalDateTime createdAt;
}
