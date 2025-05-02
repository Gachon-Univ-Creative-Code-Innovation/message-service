package com.gucci.message_service.service;

import com.gucci.message_service.config.S3Config;
import com.gucci.message_service.dto.PresignedUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
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
        String region = s3Config.getRegion();

        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String objectKey = FOLDER_PREFIX + UUID.randomUUID() + "-" + encodedName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(PRESIGNED_EXPIRATION))
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String objectUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + objectKey;

        return PresignedUrlResponseDTO.builder()
                .presignedUrl(presignedRequest.url().toString())
                .s3ObjectUrl(objectUrl)
                .build();
    }


}
