package com.cau.cc.webrtc.websocket.config;

import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.service.ChatroomApiLogicService;
import com.cau.cc.service.MatchingApiLogicService;
import com.cau.cc.webrtc.websocket.handler.WebRTCSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private MatchingApiLogicService matchingApiLogicService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    ChatroomApiLogicService chatroomApiLogicService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /** root path : ~/api 이므로 ~/api/socket **/
        registry.addHandler(new WebRTCSocketHandler(matchingApiLogicService,accountRepository,chatroomApiLogicService), "/socket")
                .setAllowedOrigins("*");
    }


}
