package com.gucci.message_service.repository;

import com.gucci.message_service.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverIdAndOrderByCreatedAtDesc(Long receiverId);

    List<Message> findByReceiverIdOrSenderId(Long receiverId, Long senderId);

    // 삭제되지 않은 메시지 전부 최신순으로 반환
    @Query("""
    SELECT m FROM Message m
    WHERE (m.receiverId = :userId OR m.senderId = :userId)
    AND (m.deletedByReceiver = false OR m.deletedBySender = false)
    ORDER BY m.createdAt DESC
    """)
    List<Message> findAllRelatedMessages(@Param("userId") Long userId);
}
