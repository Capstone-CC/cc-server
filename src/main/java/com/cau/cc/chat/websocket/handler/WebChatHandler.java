package com.cau.cc.chat.websocket.handler;

import com.cau.cc.chat.websocket.ChatRoom;
import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
import com.cau.cc.chat.websocket.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class WebChatHandler extends TextWebSocketHandler {

    private ObjectMapper objectMapper;
    private ChatService chatService;

    public WebChatHandler(ObjectMapper objectMapper, ChatService chatService) {
        this.objectMapper= objectMapper;
        this.chatService = chatService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        //웹소켓 클라이언트로부터 채팅메세지를 전달받아 채팅 메세지 객체로 전환.
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        //전달받은 메세지에 담긴 채팅방 Id로 발송 대상 채팅방 정보를 조회함
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());
        //해당 채팅방에 입장해있는 모든 클라이언트들(웹 세션)에게 타입에 따른 메세지 발송
        room.handleActions(session, chatMessage, chatService);
    }
}
