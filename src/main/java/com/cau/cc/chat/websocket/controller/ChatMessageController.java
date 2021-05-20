package com.cau.cc.chat.websocket.controller;

import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat/message")

    public void message(ChatMessage message) {
        if (MessageType.TALK.equals(message.getType())) {

            //TODO : DB의 메세지 저장
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatroomId(message.getChatroomId())
                    .userId(message.getUserId())
                    .type(message.getType())
                    .message(message.getMessage())
                    .time(LocalDateTime.now())
                    .build();
            ChatMessage newChatMessage = chatMessageRepository.save(chatMessage);

        if(MessageType.TALK.equals(message.getType())){
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatroomId(), message);
            }
        }
    }
}
