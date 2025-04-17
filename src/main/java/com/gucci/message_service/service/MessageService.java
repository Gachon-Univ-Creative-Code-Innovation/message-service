package com.gucci.message_service.service;

import com.gucci.message_service.domain.Message;
import com.gucci.message_service.dto.MessageResponseDTO;
import com.gucci.message_service.dto.MessageSendRequestDTO;
import com.gucci.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    // 메시지 전송
    public void send(Long senderId, MessageSendRequestDTO request) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .build();

        messageRepository.save(message);
    }

}
