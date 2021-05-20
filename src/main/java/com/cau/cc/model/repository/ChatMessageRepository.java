package com.cau.cc.model.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cau.cc.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT u FROM ChatMessage u  WHERE u.chatroomId = ?1")
    Page<ChatMessage> findByChatMessage(Long id, Pageable pageable);

}
