package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {
}