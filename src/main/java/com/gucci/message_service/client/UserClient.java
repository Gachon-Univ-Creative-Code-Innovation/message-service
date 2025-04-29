package com.gucci.message_service.client;

import com.gucci.common.response.ApiResponse;
import com.gucci.message_service.global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8081/api/user-service",
        configuration = FeignConfig.class) // 추후 포트 번호 변경 필요
public interface UserClient {

    @GetMapping("/{userId}/nickname")
    ApiResponse<String> getNicknameById(@PathVariable Long userId);
}
