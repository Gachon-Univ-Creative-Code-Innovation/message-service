package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.common.response.SuccessCode;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageRoomResponseDTO;
import com.gucci.message_service.service.AuthServiceHelper;
import com.gucci.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service")
public class MessageController {

    private final MessageService messageService;
    private final AuthServiceHelper authServiceHelper;

    // 방 리스트 조회
    @GetMapping("/rooms")
    public ApiResponse<List<MessageRoomResponseDTO>> getRooms(Authentication authentication) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        List<MessageRoomResponseDTO> rooms = messageService.getRooms(userId);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, rooms);
    }

    // 특정 유저와의 전체 메시지 조회
    @GetMapping("/with/{targetUserId}")
    public ApiResponse<List<MessageResponseDTO>> getMessagesWithTarget(Authentication authentication,
                                                                       @PathVariable Long targetUserId) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        List<MessageResponseDTO> messages = messageService.getMessagesWithTarget(userId, targetUserId);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, messages);
    }

    // 특정 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> deleteMessage(Authentication authentication,
                                           @PathVariable Long messageId) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        messageService.deleteMessage(userId, messageId);
        return ApiResponse.success();
    }

    // 방 나가기 (특정 유저와의 메시지 전체 삭제(논리적))
    @PostMapping("/room/exit/{targetUserId}")
    public ApiResponse<Void> exitRoomWithTarget(Authentication authentication,
                                                @PathVariable Long targetUserId) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        messageService.exitRoomWithTarget(userId, targetUserId);
        return ApiResponse.success();
    }

    // 전체 안 읽은 메시지 수 조회
    @GetMapping("/count/unread")
    public ApiResponse<Long> getUnreadCount(Authentication authentication) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        return ApiResponse.success(messageService.getAllUnreadCount(userId));
    }
}


