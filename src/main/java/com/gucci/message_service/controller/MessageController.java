package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.common.response.SuccessCode;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageSendRequestDTO;
import com.gucci.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service")
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송
    @PostMapping("/send")
    public ApiResponse<Void> send(@RequestHeader("X-User-Id") Long senderId,
                                  @RequestBody MessageSendRequestDTO message) {
        messageService.send(senderId, message);
        return ApiResponse.success();
    }

}
