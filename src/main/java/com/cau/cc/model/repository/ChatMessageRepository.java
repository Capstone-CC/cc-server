package com.cau.cc.model.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cau.cc.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT u FROM ChatMessage u  WHERE u.chatroomId.id = ?1")
    Page<ChatMessage> findByChatMessage(Long id, Pageable pageable);

//    @Query("SELECT u From ChatMessage u WHERE u.chatroomId.id = ?1 order by u.id DESC limit=1")
//    ChatMessage findByChatroomId(Long id);

    @Query("SELECT u FROM ChatMessage u where u.id = (select max(s.id) from ChatMessage s where s.chatroomId.id = ?1 )")
    ChatMessage findLastMessage(Long id);

}
