package com.gucci.message_service.repository;

import com.gucci.message_service.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 삭제되지 않은 메시지 전부 최신순으로 반환
    @Query("""
            SELECT m FROM Message m
            WHERE (m.receiverId = :userId AND m.deletedByReceiver = false)
            OR (m.senderId = :userId AND m.deletedBySender = false)
            ORDER BY m.createdAt DESC
            """)
    List<Message> findAllRelatedMessages(@Param("userId") Long userId);


    // 특정 상대와의 채팅 내역 최신순으로 반환
    @Query("""
            SELECT m FROM Message m
            WHERE (m.receiverId = :userId AND m.senderId = :targetUserId AND m.deletedByReceiver = false)
            OR (m.receiverId = :targetUserId AND m.senderId =:userId AND m.deletedBySender = false)
            ORDER BY m.createdAt DESC
            """)
    List<Message> findConversation(@Param("userId") Long userId, @Param("targetUserId") Long targetUserId);


    List<Message> findBySenderIdAndReceiverIdAndDeletedBySenderFalse(Long senderId, Long receiverId);

    List<Message> findByReceiverIdAndSenderIdAndDeletedByReceiverFalse(Long receiverId, Long senderId);


    long countBySenderIdAndReceiverIdAndIsReadFalse(Long senderId, Long receiverId);

    long countByReceiverIdAndIsReadFalseAndDeletedByReceiverFalse(Long userId);


    // 특정 상대방 별로 안 읽은 메시지 개수를 리스트로 반환
    @Query("""
            SELECT m.senderId, COUNT(m) FROM Message m
            WHERE m.receiverId = :userId AND m.senderId IN :targetIds
            AND m.isRead = false
            GROUP BY m.senderId
            """)
    List<Object[]> countUnreadMessages(@Param("userId") Long userId, @Param("targetIds") Set<Long> targetIds);

    // UPDATE Message m SET is READ = TRUE WHERE senderId = targetUserId AND receiverId = userId AND isRead = FALSE

    @Modifying(clearAutomatically = true) // 변경 후 캐시 자동 비움
    @Query("""
        UPDATE Message m SET m.isRead = true
        WHERE m.receiverId = :userId AND m.senderId = :targetUserId AND m.isRead = false
        """)
    void markAllMessagesAsRead(@Param("userId") Long userId,@Param("targetUserId") Long targetUserId);
}
