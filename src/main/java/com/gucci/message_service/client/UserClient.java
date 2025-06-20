package com.gucci.message_service.client;

import com.gucci.common.response.ApiResponse;
import com.gucci.message_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        configuration = FeignConfig.class) // 추후 포트 번호 변경 필요
public interface UserClient {

    @GetMapping("/{userId}/nickname")
    ApiResponse<String> getNicknameById(@PathVariable Long userId);


    @GetMapping("/nickname")
    ApiResponse<Map<Long, String>> getNicknamesByIds(@RequestParam List<Long> targetIds);

    @GetMapping("/profiles")
    ApiResponse<Map<Long, String>> getProfilesByIds(@RequestParam List<Long> targetIds);
}
