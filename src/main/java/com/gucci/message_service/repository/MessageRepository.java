package com.gucci.message_service.repository;

import com.gucci.message_service.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
