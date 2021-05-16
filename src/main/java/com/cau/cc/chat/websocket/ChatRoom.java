package com.cau.cc.chat.websocket;

import com.cau.cc.chat.websocket.chatmessage.ChatMessage;
import com.cau.cc.chat.websocket.service.ChatService;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;
import java.util.*;

@Data
public class ChatRoom {
    private String roomId;
    private String name;
    // 입장한 클라이언트들의 정보
    private Set<WebSocketSession> sessions = new HashSet<>();


    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name =name;
    }

    // 입장 기능 분기 처리.
    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) {
        //WebsocketSession 입장한 클라이언트 들의 정보.
        if(chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session); //채팅룸의 세션정보에 클라이언트의 세션 리스트 추가
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장");
        }
        else if(chatMessage.getType().equals(ChatMessage.MessageType.LEAVE)) {
            sessions.remove(session);
            chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장");
        }
        sendMessage(chatMessage, chatService);
        //입장시
    }

    // 대화하기 분기 처리.
    public <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session,message));
        // 채팅룸에 메세지가 도착하는 경우 채팅룸의 모든 세션에 메세지를 발송.
    }
}
