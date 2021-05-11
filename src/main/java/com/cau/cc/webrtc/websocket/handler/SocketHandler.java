package com.cau.cc.webrtc.websocket.handler;

import com.amazonaws.transform.MapEntry;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.service.MatchingApiLogicService;
import com.cau.cc.webrtc.model.MatchingAccount;
import com.cau.cc.webrtc.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    /**소켓 연결된 세션 저장**/
    Map<String,WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**offer를 보낸 사용자가 대기하는 대기방**/
    public static Map<String,MatchingAccount> matchingRoom = new ConcurrentHashMap<>();

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper mapper = new ObjectMapper();

    private MatchingApiLogicService matchingApiLogicService;

    private AccountRepository accountRepository;

    @Autowired
    public SocketHandler(MatchingApiLogicService matchingApiLogicService, AccountRepository accountRepository) {
        this.matchingApiLogicService = matchingApiLogicService;
        this.accountRepository = accountRepository;
    }

    /**
     * 클라이언트로부터 메시지를 받으면 실행됨
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        WebSocketMessage webSocketMessage = mapper.readValue(message.getPayload(),WebSocketMessage.class);

        /**Session의 해당 하는 account**/
        //TODO : 쿠키가 없는 사용자가 접근시 에러 처리 확인 필요
        Account account = null;
        try{
            Authentication authentication = (Authentication) session.getPrincipal();
            account = (Account) authentication.getPrincipal();
            account = accountRepository.findByEmail(account.getEmail());
        }catch (Exception e){
            synchronized( sessions ) {
                /**없으면 쿠기없다는 메세지 보내기**/
                sendMessage(session,new WebSocketMessage(session.getId(),"NotCookie",null,null,null));
                sessions.remove( session );
            }
        }

        MatchingAccount myMatchingAccount = null;
        MatchingAccount otherMatchingAccount = null;

        switch (webSocketMessage.getEvent()){

            //TODO: offer, answer, candidate 일때 상대방 찾아서 찾은 상대방에게 보내기
            case "offer":
                //TODO : COUNT가 0이면 매칭 시도 불가


                /**자신이 대기룸에 없으면 입장**/
                myMatchingAccount = matchingRoom.get(session.getId());
                if(myMatchingAccount == null){
                    /**대기룸 입장**/
                    myMatchingAccount = MatchingAccount.builder()
                            .id(account.getId())
                            .mySession(session)
                            .email(account.getEmail())
                            .grade(account.getGrade())
                            .count(account.getCount())
                            .majorName(account.getMajorName())
                            .gender(account.getGender())
                            .matchingState(false)
                            .build();
                    matchingRoom.put(session.getId(),myMatchingAccount);
                }

                //매칭상대 찾은 상태값 0: 몾찾음, 1: 찾음
                int check = 0;

                //TODO: 상대방 찾기 추후 수정 필요
                /** 1. 전체 웹소켓 세션을 돌면서 **/
                for( Map.Entry<String,WebSocketSession> otherSession : sessions.entrySet()){
                    /**2. 대기룸에 있는 사용자 중에서 자신이 아닌 상대방 찾고**/
                    otherMatchingAccount = matchingRoom.get(otherSession.getKey());
                    /**if 체크 순서 중요! **/
                    if( otherMatchingAccount != null && !session.getId().equals(otherMatchingAccount.getMySession().getId())) {

                        /**3. 찾은 상대방이 이미 매칭된 상태 또는 자신과 같은 성별라면 continue**/
                        if(otherMatchingAccount.getPeerSessionId() != null
                        || otherMatchingAccount.getGender().equals(myMatchingAccount.getGender())){
                            continue;
                        }

                        /**4.매칭안된 사용자이고 자신과 다른 성별이면 조건의 맞는지 확인**/
//                        String myWantGrade = webSocketMessage.getGrade(); // 자신이 원하는 학년
//                        String myWantMajor = webSocketMessage.getGrade(); // 자신이 원하지 않는 학과
//                        if(myWantGrade.equals(otherMatchingAccount.getGrade())
//                                && !myWantMajor.equals(otherMatchingAccount.getMajorName())){
//                            //둘다 맞는 경우
//
//                        } else if(myWantGrade.equals(otherMatchingAccount.getGrade())
//                                || !myWantMajor.equals(otherMatchingAccount.getMajorName())){
//                            //하나만 맞는경우
//
//                        } else {
//                            //둘다 안맞는 경우
//
//                        }


                        /**5. 조건의 맞다면 각자 자신의 상대 sessionid 저장후**/
                        myMatchingAccount.setPeerSessionId(otherSession.getKey()); // 나의 객체의 상대방 id 저장
                        otherMatchingAccount.setPeerSessionId(session.getId()); //상대방 객체의 나의 id 저장

                        /**6. 자신의 offer 보내기 **/
                        sendMessage(otherSession.getValue(), webSocketMessage);
                        check = 1;
                        break;
                    }
                }

                if(check == 0){
                    /**없으면 대기하라는 메세지 보내기**/
                    sendMessage(session,new WebSocketMessage(session.getId(),"wait",null,null,null));
                    break;
                } else{
                    break;
                }

                //TODO : offer를 받아야만 answer를 보내는지 확인
            /**자신의 peerSessionId에 해당하는 사람에게 anwser 보내기**/
            case "answer":

                /**1. 자신의 객체 찾고**/
                myMatchingAccount = matchingRoom.get(session.getId());

                /**2. 자신과 연결된 상대방에게 answer 전달 **/
                MatchingAccount other = matchingRoom.get(myMatchingAccount.getPeerSessionId());
                if(other != null){
                    sendMessage(other.getMySession(),webSocketMessage);
                }
                break;



            /**연결 된 상대방에게 candidate 메시지 보내기**/
            case "candidate":

                /**1. 자신의 객체 찾고**/
                myMatchingAccount = matchingRoom.get(session.getId());
                /**2. 자신의 객체가 없거나 자신과 연결된 상대가 없으면 break;**/
                if(myMatchingAccount == null || myMatchingAccount.getPeerSessionId() == null){
                    sendMessage(session,new WebSocketMessage(session.getId(),"wait",null,null,null));
                    break;
                }
                /**3. 자신과 연결된 상대방에게 answer 전달 **/
                otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());
                if(otherMatchingAccount != null){
                    sendMessage(otherMatchingAccount.getMySession(),webSocketMessage);
                }
                break;

            case "connect":
                //TODO: connect를 보낸 사용자 state를 1로 바꾸기
                myMatchingAccount = matchingRoom.get(session.getId());
                myMatchingAccount.setMatchingState(true);

                //TODO: 상대방 stete 확인
                otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());
                if (otherMatchingAccount.isMatchingState()){

                    /**각각 count 값 1씩 감소**/
                    Account tmp = accountRepository.findByEmail(myMatchingAccount.getEmail());
                    tmp.setCount(tmp.getCount()-1);
                    accountRepository.save(tmp);
                    Account tmp2 = accountRepository.findByEmail(otherMatchingAccount.getEmail());
                    tmp2.setCount(tmp2.getCount()-1);
                    accountRepository.save(tmp2);

                    myMatchingAccount.setCount(myMatchingAccount.getCount()-1);
                    otherMatchingAccount.setCount(otherMatchingAccount.getCount()-1);

                    //TODO : 매칭룸 생성
                    MatchingApiRequest request = MatchingApiRequest.builder()
                            .manUserState(0)
                            .womanUserState(0)
                            .time(LocalDateTime.now())
                            .manId(myMatchingAccount.getId())
                            .womanId(otherMatchingAccount.getId())
                            .build();
                    matchingApiLogicService.create(request);
                }
                break;


                /**수락 요청시**/
            case "accept":
                //TODO : 1. 생성된 매칭 테이블에서 세션에 해당하는 ID의 state 바꾸고

                /**1. 수락을 보낸 사용자**/
                myMatchingAccount = matchingRoom.get(session.getId());

                /** 2.매칭 테이블에서 자신의 이메일에 해당하는 recore 받아와서 **/


                /**3. 자신이 남자이면 남자 state 변경, 여자이면 여자 state 변경**/


                /**4. 상대방의 수락상태 확인 **/
                //if(){
                    //TODO : 상대방도 수락이므로 chatroom record 만들기
                //}

                /**수락 안했으므로 패스**/
                break;
        }
    }

    /**
     * 모든 클라이언트를 추적 할 수 있도록 수신 된 세션을 세션 목록에 추가
     * 클라이언트가 socket 요청 보내는데 그 때 해당 메소드 실행됨
     * 즉, 클라이언트가 socket 요청 -> 해당메소드 실행
     * or 소켓 에러 발생시 실행
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        /**1. 접근한 사용자가 Session룸에 있는지 확인**/
        WebSocketSession s = sessions.get(session.getId());
        if(s == null){
            /**2. 업다면 대기 방인 mathcingRoom에 
             * session Id(String)를 key값으로 session 객체 넣기**/
            sessions.put(session.getId(),session);
        }



    }

    /**
     * WebSocket 종료시 실행된다.
     * 브라우저가 연결을 닫으면 이 메서드가 호출되고 세션이 세션 목록에서 제거
     * 클라이언트가 소켓 끊으면 실행된다.
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

    /**메시지 보내기**/
    private void sendMessage(WebSocketSession webSocketSession, WebSocketMessage webSocketMessage) {
        try {
            String json = mapper.writeValueAsString(webSocketMessage);
            webSocketSession.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            synchronized( sessions ) {
                sessions.remove( webSocketSession );
            }
        }
    }
}
