package com.cau.cc.chat.websocket.controller;

import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
import com.cau.cc.chat.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final ChatService chatService;

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message){
        if(ChatMessage.MessageType.TALK.equals(message.getType())){
            messagingTemplate.convertAndSend("/sub/chat/room/"+message.getRoomId(),message);
        }
    }
}
