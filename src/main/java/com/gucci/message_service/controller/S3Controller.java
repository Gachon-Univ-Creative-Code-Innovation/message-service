package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.message_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ApiResponse<String> getPresignedUrl(@RequestParam String fileName) {
        String objectKey = "message/" + UUID.randomUUID() + "-" + fileName;
        String presignedUrl = s3Service.generatePresignedUploadUrl(objectKey);
        return ApiResponse.success(presignedUrl);
    }
}
