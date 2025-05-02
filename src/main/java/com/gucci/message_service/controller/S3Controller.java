package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.message_service.dto.PresignedUrlResponseDTO;
import com.gucci.message_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ApiResponse<PresignedUrlResponseDTO> getPresignedUrl(@RequestParam String fileName) {
        PresignedUrlResponseDTO url = s3Service.generatePresignedUploadUrl(fileName);
        return ApiResponse.success(url);
    }
}
