package com.gucci.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Getter
@AllArgsConstructor
@Builder
public class WebSocketReadEventDTO {
    @Default
    private String type = "READ";
    private Long messageId;
    private Long readerId;
}
