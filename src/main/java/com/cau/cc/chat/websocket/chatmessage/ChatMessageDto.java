package com.cau.cc.chat.websocket.chatmessage;

import com.cau.cc.model.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;

    private Long chatroomId;

    private Long userId;

    private MessageType type; // 메시지 타입

    private String message; // 메시지
    private LocalDateTime time;
}
