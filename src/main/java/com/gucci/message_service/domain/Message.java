package com.gucci.message_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private Long senderId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isRead;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deletedByReceiver;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deletedBySender;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void markAsDeleteByReceiver() {
        this.deletedByReceiver = true;
    }

    public void markAsDeleteBySender() {
        this.deletedBySender = true;
    }

}
