package com.gucci.message_service.controller;

import com.gucci.common.response.ApiResponse;
import com.gucci.common.response.SuccessCode;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageRoomResponseDTO;
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

    // 방 리스트 조회
    @GetMapping("/rooms")
    public ApiResponse<List<MessageRoomResponseDTO>> getRooms(@RequestHeader("X-User-Id") Long userId) {
        List<MessageRoomResponseDTO> rooms = messageService.getRooms(userId);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, rooms);
    }

    // 특정 유저와의 전체 메시지 조회
    @GetMapping("/with/{targetUserId}")
    public ApiResponse<List<MessageResponseDTO>> getMessagesWithTarget(@RequestHeader("X-User-Id") Long userId,
                                                             @PathVariable Long targetUserId) {
        List<MessageResponseDTO> messages = messageService.getMessagesWithTarget(userId, targetUserId);
        return ApiResponse.success(SuccessCode.DATA_FETCHED, messages);
    }

    // 특정 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> deleteMessage(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long messageId) {
        messageService.deleteMessage(userId, messageId);
        return ApiResponse.success();
    }

    // 방 나가기 (특정 유저와의 메시지 전체 삭제(논리적))
    @PostMapping("/room/exit/{targetUserId}")
    public ApiResponse<Void> exitRoomWithTarget(@RequestHeader("X-User-Id") Long userId,
                                                @PathVariable Long targetUserId) {
        messageService.exitRoomWithTarget(userId, targetUserId);
        return ApiResponse.success();
    }


}
