package com.cau.cc.chat.websocket.controller;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.MessageType;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@Api(tags = "메세지 보내기")
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat/message")

    public void message(ChatMessage message) {
        if (MessageType.TALK.equals(message.getType())) {
            if(MessageType.TALK.equals(message.getType())){
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatroomId(), message);
            }
            //TODO : DB의 메세지 저장
            Chatroom chatroom = message.getChatroomId();
            Account account = message.getUserId();
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatroomId(chatroom)
                    .userId(account)
                    .type(message.getType())
                    .message(message.getMessage())
                    .time(LocalDateTime.now())
                    .build();
            chatMessageRepository.save(chatMessage);

        }
    }
}
