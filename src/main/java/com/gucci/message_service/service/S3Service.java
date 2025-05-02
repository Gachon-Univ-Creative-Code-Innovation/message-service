package com.gucci.message_service.service;

import com.gucci.message_service.config.S3Config;
import com.gucci.message_service.dto.PresignedUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private static final String FOLDER_PREFIX = "message/";
    private static final long PRESIGNED_EXPIRATION = 30;

    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    public PresignedUrlResponseDTO generatePresignedUploadUrl(String fileName) {
        String bucketName = s3Config.getBucketName();

        String objectKey = FOLDER_PREFIX + UUID.randomUUID() + "-" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(PRESIGNED_EXPIRATION))
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return PresignedUrlResponseDTO.builder()
                .presignedUrl(presignedRequest.url().toString())
                .s3ObjectUrl(objectKey)
                .build();
    }


    public String getPresignedUrl(String objectUrl) {
        String bucketName = s3Config.getBucketName();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectUrl)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(PRESIGNED_EXPIRATION))
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
