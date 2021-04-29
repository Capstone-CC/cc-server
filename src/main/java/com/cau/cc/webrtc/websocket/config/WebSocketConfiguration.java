package com.cau.cc.webrtc.websocket.config;

import com.cau.cc.webrtc.websocket.handler.SocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /** root path : ~/api 이므로 ~/api/socket **/
        registry.addHandler(new SocketHandler(), "/socket")
                .setAllowedOrigins("*");
    }
}