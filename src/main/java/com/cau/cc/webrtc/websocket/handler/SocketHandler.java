package com.cau.cc.webrtc.websocket.handler;

import com.amazonaws.transform.MapEntry;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    Map<String,WebSocketSession> sessions = new HashMap<>();

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

            //TODO: offer, answer, candidate 일때 상대방 찾아서 찾은 상대방에게 보내기
            case "offer":
                /**자신이 대기룸에 없으면 입장**/
                MatchingAccount matchingAccount = matchingRoom.get(session.getId());
                if(matchingAccount == null){
                    /**대기룸 입장**/
                    matchingAccount = MatchingAccount.builder()
                            .mySession(session)
                            .email(account.getEmail())
                            .grade(account.getGrade())
                            .majorName(account.getMajorName())
                            .matchingState(false)
                            .acceptState(false)
                            .build();
                    matchingRoom.put(session.getId(),matchingAccount);
                }

                //TODO: 상대방 찾기
                /** 1. 전체 웹소켓 세션을 돌면서 **/
                for( Map.Entry<String,WebSocketSession> otherSession : sessions.entrySet()){
                    /**2. 대기룸에 있는 사용자 중에서 자신이 아닌 상대방 찾고**/
                    MatchingAccount other = matchingRoom.get(otherSession.getKey());
                    /**if 체크 순서 중요! **/
                    if( other != null && !session.getId().equals(other.getMySession().getId())) {

                        /**3. 찾으면 각자 자신의 상대 sessionid 저장후**/
                        matchingAccount.setPeerSessionId(otherSession.getKey()); // 나의 객체의 상대방 id 저장
                        other.setPeerSessionId(session.getId()); //상대방 객체의 나의 id 저장

                        /**4. 자신의 offer 보내기 **/
                        sendMessage(otherSession.getValue(), webSocketMessage);

                        break;
                    }
                }

                //TODO: 없으면 대기하라는 메세지 보내기
                sendMessage(session,new WebSocketMessage(session.getId(),"wait",null,null,null));
                break;


            case "answer":
                //TODO: 자신의 peerSessionId에 해당하는 사람에게 anwser 보내기
                /**1. 자신의 객체 찾고**/
                MatchingAccount me = matchingRoom.get(session.getId());
                /**2. 자신과 연결된 상대방에게 answer 전달 **/
                MatchingAccount other = matchingRoom.get(me.getPeerSessionId());
                if(other != null){
                    sendMessage(other.getMySession(),webSocketMessage);
                }
                break;

                //TODO: 연결된 이후
            case "candidate":
                //TODO: 매칭룸 생성

                //TODO: 연결 된 상대방에게 상대방에게 메시지 보내기 추후 for문 삭제
                /**1. 자신의 객체 찾고**/
                MatchingAccount my = matchingRoom.get(session.getId());
                /**2. 자신과 연결된 상대방에게 answer 전달 **/
                MatchingAccount connectOther = matchingRoom.get(my.getPeerSessionId());
                if(connectOther != null){
                    sendMessage(connectOther.getMySession(),webSocketMessage);
                }
                break;

//                for (Map.Entry<String,WebSocketSession> otherSessionElem : sessions.entrySet()) {
//                    if (otherSessionElem.getValue().isOpen() && !session.getId().equals(otherSessionElem.getKey())) {
//                        sendMessage(otherSessionElem.getValue(),webSocketMessage);
//                    }
//                }

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
        sessions.put(session.getId(),session);



    }

    /**
     * WebSocket 종료시 실행된다.
     * 브라우저가 연결을 닫으면 이 메서드가 호출되고 세션이 세션 목록에서 제거
     */
    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
     //   logger.debug("[ws] sesstion remove");
        sessions.remove(session.getId());

        /**종료한 Session이 대기룸에 있으면 제거**/
        if(matchingRoom.get(session.getId()) != null){
            matchingRoom.remove(session.getId());
        }

        //TODO: 연결된 사용자도 끊기
    }

    private void sendMessage(WebSocketSession webSocketSession, WebSocketMessage webSocketMessage) {
        try {
            String json = mapper.writeValueAsString(webSocketMessage);
            webSocketSession.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            //logger.debug("An error occured: {}", e.getMessage());
        }
    }
}
