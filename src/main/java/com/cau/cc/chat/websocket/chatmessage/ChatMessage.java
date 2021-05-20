package com.cau.cc.chat.websocket.chatmessage;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(exclude = {"userId","chatroomId"})
@Entity
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER,TALK,LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chatroom_id")
    @ManyToOne
    private Chatroom chatroomId;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private Account userId;

    private MessageType type; // 메시지 타입
    private String message; // 메시지
    private LocalDateTime time;
}