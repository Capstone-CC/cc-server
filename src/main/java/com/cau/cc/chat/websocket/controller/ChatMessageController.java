package com.cau.cc.chat.websocket.controller;

import com.cau.cc.chat.websocket.chatmessage.ChatMessageDto;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.ChatMessage;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.MessageType;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.model.repository.ChatRoomRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Api(tags = "메세지 보내기")
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AccountRepository accountRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        if(MessageType.TALK.equals(message.getType())){
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatroomId(), message);
        }
        else if(MessageType.LEAVE.equals(message.getType())){
            Optional<Account> account = accountRepository.findById(message.getUserId());
            message.setMessage(account.get().getNickName() + "님이 채팅방을 떠났습니다.");
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatroomId(), message);
        }

        Optional<Chatroom> chatroom = chatRoomRepository.findById(message.getChatroomId());
        Optional<Account> account = accountRepository.findById(message.getUserId());

        if(!chatroom.isEmpty() && !account.isEmpty()){
            //TODO : DB의 메세지 저장
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatroomId(chatroom.get())
                    .userId(account.get())
                    .type(message.getType())
                    .message(message.getMessage())
                    .time(LocalDateTime.now())
                    .build();
            chatMessageRepository.save(chatMessage);
        }
    }
}
