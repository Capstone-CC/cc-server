package com.cau.cc.chat.websocket.controller;

<<<<<<< HEAD
import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.Report;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.response.AccountProfileApiResponse;
import com.cau.cc.model.repository.ChatMessageRepository;
=======
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.MessageType;
>>>>>>> 6fabe23565d341f9f1fd5b5170061674bd26f64b
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
    public void message(ChatMessage message){
<<<<<<< HEAD
        if(ChatMessage.MessageType.TALK.equals(message.getType())){

            //TODO : DB의 메세지 저장
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatroomId(message.getChatroomId())
                    .userId(message.getUserId())
                    .type(message.getType())
                    .message(message.getMessage())
                    .time(LocalDateTime.now())
                    .build();
            ChatMessage newChatMessage = chatMessageRepository.save(chatMessage);
=======
        if(MessageType.TALK.equals(message.getType())){
            //TODO : DB의 메세지 저장

>>>>>>> 6fabe23565d341f9f1fd5b5170061674bd26f64b

            messagingTemplate.convertAndSend("/sub/chat/room/"+message.getChatroomId(),message);
        }
    }

}
