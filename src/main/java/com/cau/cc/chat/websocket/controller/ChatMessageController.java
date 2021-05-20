package com.cau.cc.chat.websocket.controller;

import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message){
        if(MessageType.TALK.equals(message.getType())){
            //TODO : DB의 메세지 저장


            messagingTemplate.convertAndSend("/sub/chat/room/"+message.getChatroomId(),message);
        }
    }

}
