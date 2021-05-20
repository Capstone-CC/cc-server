package com.cau.cc.model.network.response;

import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
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
public class ChatMessageApiResponse {

    private Long id;

    private Chatroom chatroomId;

    private Account userId;

    private com.cau.cc.chat.websocket.chatmessage.ChatMessage.MessageType type; // 메시지 타입
    private String message; // 메시지
    private LocalDateTime time;

}
