package com.gucci.message_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageSendRequestDTO {
    private Long receiverId;
    private String content;
}
