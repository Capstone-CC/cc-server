package com.cau.cc.chat.websocket.config;

import com.cau.cc.chat.websocket.handler.WebChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.logging.SocketHandler;

@Configuration
@EnableWebSocket
public class WebChatSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /** root path : ~/api 이므로 ~/api/chat **/
        registry.addHandler(new WebChatHandler(), "/chat")
                .setAllowedOrigins("*");
    }
}