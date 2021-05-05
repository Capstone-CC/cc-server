package com.cau.cc.webrtc.websocket.handler;

import com.cau.cc.model.entity.Account;
import com.cau.cc.webrtc.model.MatchingAccount;
import com.cau.cc.webrtc.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public static Map<String,MatchingAccount> matchingRoom = new HashMap<>();

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper mapper = new ObjectMapper();

    /**
     * 클라이언트로부터 메시지를 받으면 목록의 모든 클라이언트 세션을 반복하고
     * 보낸 사람의 세션 ID를 비교하여 보낸 사람을 제외한 다른 모든 클라이언트에게 메시지를 보낸다.
     *  Client가 Offer하는 경우 실행 됨
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        WebSocketMessage webSocketMessage = mapper.readValue(message.getPayload(),WebSocketMessage.class);

        /**Session의 해당 하는 account**/
        Authentication authentication = (Authentication) session.getPrincipal();
        Account account = (Account) authentication.getPrincipal();

        switch (webSocketMessage.getEvent()){
//            //TODO: join 일때 대기방에 들어가기
//            case "join":
//                MatchingAccount matchingAccount = MatchingAccount.builder()
//                        .email(account.getEmail())
//                        .grade(account.getGrade())
//                        .majorName(account.getMajorName())
//                        .matchingState(false)
//                        .acceptState(false)
//                        .build();
//                matchingRoom.put(session.getId(),matchingAccount);
//                break;


            //TODO: offer, answer, candidate 일때 상대방 찾아서 찾은 상대방에게 보내기
            case "offer":
            case "answer":
                //TODO: 상대방 찾기

                //TODO: 자신의 peerSessionId 저장 후 상대방의 peerSessionId를 자신으로 저장

                //TODO: 상대방에게 메시지 보내기 추후 for문 삭제
                for (WebSocketSession webSocketSession : sessions) {
                    if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                        String json = mapper.writeValueAsString(webSocketMessage);
                        webSocketSession.sendMessage(new TextMessage(json));
                    }
                }
                break;

                //TODO: 연결된 이후
            case "candidate":
                //TODO: 매칭룸 생성

                //TODO: 상대방에게 메시지 보내기 추후 for문 삭제
                for (WebSocketSession webSocketSession : sessions) {
                    if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                        String json = mapper.writeValueAsString(webSocketMessage);
                        webSocketSession.sendMessage(new TextMessage(json));
                    }
                }
                break;

                /**수락 요청시**/
            case "accept":
                /** 자신의 수락상태 true로 바꾸고 **/
                MatchingAccount acceptUser = matchingRoom.get(session.getId());
                acceptUser.setAcceptState(true);

                /** 상대방의 수락상태 확인 **/
                MatchingAccount peerUser = matchingRoom.get(acceptUser.getPeerSessionId());
                if(peerUser.isAcceptState()){
                    //TODO : 상대방도 수락이므로 Mathching 테이블 만들기

                }
                /**수락 안했으므로 패스**/
                break;
        }
    }

    /**
     * 모든 클라이언트를 추적 할 수 있도록 수신 된 세션을 세션 목록에 추가
     * 클라이언트가 index.html 접속 하면 socket 보내는데 그 때 해당 메소드 실행됨
     * 즉, 클라이언트가 localhost:8080 접속 -> index.html -> client.js의 socket 요청 -> 해당메소드 실행
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        /**대기 방인 mathcingRoom에 session Id(String)를 key값으로 MatchingAccount 객체 넣기**/
        sessions.add(session);

        /**Session의 해당 하는 account**/
        Authentication authentication = (Authentication) session.getPrincipal();
        Account account = (Account) authentication.getPrincipal();

        /**대기룸 입장**/
        MatchingAccount matchingAccount = MatchingAccount.builder()
                .email(account.getEmail())
                .grade(account.getGrade())
                .majorName(account.getMajorName())
                .matchingState(false)
                .acceptState(false)
                .build();
        matchingRoom.put(session.getId(),matchingAccount);

    }

    /**
     * 브라우저가 연결을 닫으면 이 메서드가 호출되고 세션이 세션 목록에서 제거
     */
    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
     //   logger.debug("[ws] sesstion remove");
        sessions.remove(session.getId());
    }
}
