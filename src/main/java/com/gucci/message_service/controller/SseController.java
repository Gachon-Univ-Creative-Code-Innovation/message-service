package com.gucci.message_service.controller;

import com.gucci.message_service.service.AuthServiceHelper;
import com.gucci.message_service.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service/sse")
public class SseController {
    private final SseEmitterManager sseEmitterManager;
    private final AuthServiceHelper authServiceHelper;

    // SSE 연결
    @GetMapping("/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        return sseEmitterManager.connect(userId);
    }

}
