package com.gucci.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PresignedUrlResponseDTO {
    private String presignedUrl;
    private String s3ObjectUrl;
}
