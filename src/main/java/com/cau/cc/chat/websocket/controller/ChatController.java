package com.cau.cc.chat.websocket.controller;

import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
// publisher 구현
public class ChatController {

    //private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message") //websocket으로 들어오는 메세지 발행을 처리.
    public void message(ChatMessage message) {
        if(ChatMessage.MessageType.ENTER.equals((message.getType())))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");

        // /sub/chat/room/{roomId} 구독하고 있다가 메시지가 전달되면 화면에 출력.
        // /sub/chat/room/{roomId} 채팅룸을 구분하는 값
        // pub/sub에서 Topic의 역할
        //messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
