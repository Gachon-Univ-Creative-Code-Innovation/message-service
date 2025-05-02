package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.message_service.dto.PresignedUrlResponseDTO;
import com.gucci.message_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ApiResponse<PresignedUrlResponseDTO> createPresignedUrl(@RequestParam String fileName) {
        PresignedUrlResponseDTO url = s3Service.generatePresignedUploadUrl(fileName);
        return ApiResponse.success(url);
    }

    @GetMapping("/image-url")
    public ApiResponse<String> getPresignedUrl(@RequestParam String objectUrl) {
        String presignedUrl = s3Service.getPresignedUrl(objectUrl);
        return ApiResponse.success(presignedUrl);
    }
}
