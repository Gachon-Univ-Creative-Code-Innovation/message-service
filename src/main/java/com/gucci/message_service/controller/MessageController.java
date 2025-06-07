package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.common.response.SuccessCode;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageRoomResponseDTO;
import com.gucci.message_service.service.AuthServiceHelper;
import com.gucci.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-service")
public class MessageController {

    private final MessageService messageService;
    private final AuthServiceHelper authServiceHelper;
    private final Environment environment;

    //health check
    @GetMapping("/health-check")
    public String healthCheck() {
        String port = environment.getProperty("local.server.port");
        return  "Message Service is up and running on port: " + port;
    }

    // 방 리스트 조회
    @GetMapping("/rooms")
    public ApiResponse<List<MessageRoomResponseDTO>> getRooms(Authentication authentication) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        List<MessageRoomResponseDTO> rooms = messageService.getRooms(userId);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, rooms);
    }

    // 특정 유저와의 전체 메시지 조회
    @GetMapping("/with/{targetUserId}")
    public ApiResponse<Page<MessageResponseDTO>> getMessagesWithTarget(Authentication authentication,
                                                                       @PathVariable Long targetUserId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        Page<MessageResponseDTO> messages = messageService.getMessagesWithTarget(userId, targetUserId, page, size);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, messages);
    }

    // 메시지 검색
    @GetMapping("/with/{targetUserId}/search")
    public ApiResponse<List<MessageResponseDTO>> searchMessages(Authentication authentication,
                                                                @PathVariable Long targetUserId,
                                                                @RequestParam String keyword) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        List<MessageResponseDTO> searchedMessages = messageService.searchMessagesWithTarget(userId, targetUserId, keyword);
        return ApiResponse.success(searchedMessages);
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

    // 메시지 읽음 처리
    @PatchMapping("{messageId}/read")
    public ApiResponse<Void> readMessage(Authentication authentication,
                                         @PathVariable Long messageId) {
        Long userId = authServiceHelper.getCurrentUserId(authentication);
        messageService.markAsRead(messageId, userId);
        return ApiResponse.success();
    }
}


