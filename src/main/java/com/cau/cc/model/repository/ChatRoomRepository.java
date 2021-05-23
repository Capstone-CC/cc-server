package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {

//    @Query("SELECT u FROM Chatroom u WHERE u.manId=?1 or u.womanId=?1")
//    public Chatroom findAll(Long id);


}
