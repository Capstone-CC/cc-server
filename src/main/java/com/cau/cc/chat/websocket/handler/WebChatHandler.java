package com.cau.cc.chat.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebChatHandler extends TextWebSocketHandler {

    /**
     * 채팅목록에서 채팅방 입장 -> 세션 추가
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    /**
     * 채팅방에서 out -> 세션 제거
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //TODO : 자신의 세션 제거 후 상대방 체크
        super.afterConnectionClosed(session, status);
    }
}
