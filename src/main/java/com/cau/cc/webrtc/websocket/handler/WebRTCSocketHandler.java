package com.cau.cc.webrtc.websocket.handler;

import com.amazonaws.transform.MapEntry;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.network.response.MatchingApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.service.ChatroomApiLogicService;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebRTCSocketHandler extends TextWebSocketHandler {

    /**소켓 연결된 세션 저장**/
    /**session ID - session**/
    Map<String,WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**offer를 보낸 사용자가 대기하는 대기방**/
    /**session ID - MatchingAccount**/
    public static Map<String,MatchingAccount> matchingRoom = new ConcurrentHashMap<>();

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper mapper = new ObjectMapper();

    private MatchingApiLogicService matchingApiLogicService;

    private AccountRepository accountRepository;

    private ChatroomApiLogicService chatroomApiLogicService;

    // 3분, 5분, 7분, 10분을 초단위로
    private int[] randomNum = {180,300,420,600};


    @Autowired
    public WebRTCSocketHandler(MatchingApiLogicService matchingApiLogicService,
                               AccountRepository accountRepository,
                               ChatroomApiLogicService chatroomApiLogicService) {
        this.matchingApiLogicService = matchingApiLogicService;
        this.accountRepository = accountRepository;
        this.chatroomApiLogicService = chatroomApiLogicService;

    }


    public int random(){
        Random random = new Random();
        int randomIdx = random.nextInt(4);
        return this.randomNum[randomIdx];
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
                sendMessage(session,new WebSocketMessage(session.getId(),"notcookie",null,null));
                sessions.remove( session );
            }
        }

        MatchingAccount myMatchingAccount = null;
        MatchingAccount otherMatchingAccount = null;

        switch (webSocketMessage.getEvent()){

            //TODO: offer, answer, candidate 일때 상대방 찾아서 찾은 상대방에게 보내기
            case "offer":
                //TODO : COUNT가 0이면 매칭 시도 불가
                if(account.getCount() <= 0){
                    sendMessage(session,new WebSocketMessage(session.getId(),"fail",null,null));
                    break;
                }

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
                            .nickName(account.getNickName())
                            .matchingState(false)
                            .wantGrade(webSocketMessage.getOption().getGrade())
                            .selectMajor(webSocketMessage.getOption().getMajorName())
                            .majorState(webSocketMessage.getOption().getMajorState())
                            .build();
                    matchingRoom.put(session.getId(),myMatchingAccount);
                }

                //매칭상대 찾은 상태값 0: 몾찾음, 1: 찾음
                int check = 0;
                int start = 1;

                /** 1. 전체 웹소켓 세션을 돌면서 **/
                for( Map.Entry<String,WebSocketSession> otherSession : sessions.entrySet()){

                    start = 1;

                    /**2. 대기룸에 있는 사용자 중에서 자신이 아닌 상대방 찾고**/
                    otherMatchingAccount = matchingRoom.get(otherSession.getKey());
                    /**if 체크 순서 중요! **/
                    if( otherMatchingAccount != null && !session.getId().equals(otherMatchingAccount.getMySession().getId())) {

                        /**3. 찾은 상대방이 이미 매칭된 상태 또는 자신과 같은 성별이라면 continue**/
                        if(otherMatchingAccount.getPeerSessionId() != null
                                || otherMatchingAccount.getGender().equals(myMatchingAccount.getGender())){
                            start = 0;
                            continue;
                        }

                        /**4.매칭안된 사용자이고 자신과 다른 성별이면 조건의 맞는지 확인**/

                        /**grade가 0이면 전체선택, Majorstate가 0이면 전체선택**/
                        if(myMatchingAccount.getWantGrade() != 0 && myMatchingAccount.getMajorState() != 0){ // 학년, 학과모두 상관 있으면

                            if(!compareToWantGrade(myMatchingAccount,otherMatchingAccount)
                            || !compareToMajor(myMatchingAccount, otherMatchingAccount)){
                                //둘중 하나라도 해당 안되면
                                start = 0;
                                continue;
                            }

                            //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                            //상대방 기준
                            if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                if(!compareToWantGrade(otherMatchingAccount, myMatchingAccount)
                                        || !compareToMajor(otherMatchingAccount, myMatchingAccount)){
                                    //둘중 하나라도 해당 안되면
                                    start = 0;
                                    continue;
                                }
                            }
                            else if(otherMatchingAccount.getWantGrade() == 0 && otherMatchingAccount.getMajorState() != 0){//학년 상관X, 학과 상관O
                                if(!compareToMajor(otherMatchingAccount,myMatchingAccount)){ //state에 따른 학과 매칭 실패
                                    start = 0;
                                    continue;
                                }
                            } else if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() == 0){//학과 상관X, 학년 상관
                                if (!compareToWantGrade(otherMatchingAccount,myMatchingAccount)) { //학년 불일치 pass
                                    start = 0;
                                    continue;
                                }
                            }

                            /**grade가 0으로 전체선택이고 Majorstate가 0이 아닌 상황**/
                        } else if(myMatchingAccount.getWantGrade() == 0 && myMatchingAccount.getMajorState() != 0) { //학년 상관X, 학과 상관O
                            if(!compareToMajor(myMatchingAccount,otherMatchingAccount)){ //state에 따른 학과 매칭 실패
                                start = 0;
                                continue;
                            }

                            //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                            //상대방 기준
                            if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                if(!compareToWantGrade(otherMatchingAccount, myMatchingAccount)
                                        || !compareToMajor(otherMatchingAccount, myMatchingAccount)){
                                    //둘중 하나라도 해당 안되면
                                    start = 0;
                                    continue;
                                }
                            }
                            else if(otherMatchingAccount.getWantGrade() == 0 && otherMatchingAccount.getMajorState() != 0){//학년 상관X, 학과 상관O
                                if(!compareToMajor(otherMatchingAccount,myMatchingAccount)){ //state에 따른 학과 매칭 실패
                                    start = 0;
                                    continue;
                                }
                            } else if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() == 0){//학과 상관X, 학년 상관
                                if (!compareToWantGrade(otherMatchingAccount,myMatchingAccount)) { //학년 불일치 pass
                                    start = 0;
                                    continue;
                                }
                            }

                            /**grade가 0 전체선택 아니고 Majorstate가 0인 상황**/
                        } else if (myMatchingAccount.getWantGrade() != 0 && myMatchingAccount.getMajorState() == 0) { //학과 상관X, 학년 상관
                            /**상대방의 학년과 내가 원하는 학년이 맞는지 비교 **/
                            if (!compareToWantGrade(myMatchingAccount,otherMatchingAccount)) { //학년 불일치 pass
                                start = 0;
                                continue;
                            }

                            //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                            //상대방 기준
                            if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                if(!compareToWantGrade(otherMatchingAccount, myMatchingAccount)
                                        || !compareToMajor(otherMatchingAccount, myMatchingAccount)){
                                    //둘중 하나라도 해당 안되면
                                    start = 0;
                                    continue;
                                }
                            }
                            else if(otherMatchingAccount.getWantGrade() == 0 && otherMatchingAccount.getMajorState() != 0){//학년 상관X, 학과 상관O
                                if(!compareToMajor(otherMatchingAccount,myMatchingAccount)){ //state에 따른 학과 매칭 실패
                                    start = 0;
                                    continue;
                                }
                            } else if(otherMatchingAccount.getWantGrade() != 0 && otherMatchingAccount.getMajorState() == 0){//학과 상관X, 학년 상관
                                if (!compareToWantGrade(otherMatchingAccount,myMatchingAccount)) { //학년 불일치 pass
                                    start = 0;
                                    continue;
                                }
                            }

                        }

                        /**조건 불일치**/
                        if(start == 0){
                            break;
                        }

                        /**조건의 일치**/

                        //바로 메시지 보내기
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
                    sendMessage(session,new WebSocketMessage(session.getId(),"wait",null,null));
                    break;
                } else{
                    break;
                }

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
                    sendMessage(session,new WebSocketMessage(session.getId(),"wait",null,null));
                    break;
                }
                /**3. 자신과 연결된 상대방에게 answer 전달 **/
                otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());
                if(otherMatchingAccount != null){
                    sendMessage(otherMatchingAccount.getMySession(),webSocketMessage);
                }
                break;


            case "connect":
                /** connect를 보낸 사용자 state를 1로 바꾸기**/
                myMatchingAccount = matchingRoom.get(session.getId());
                myMatchingAccount.setMatchingState(true);

                /** 상대방 stete 확인 **/
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

                    /** 매칭 룸 생성 **/
                    MatchingApiRequest request = MatchingApiRequest.builder()
                            .manUserState(0)
                            .womanUserState(0)
                            .time(LocalDateTime.now().withNano(0))
                            .build();

                    /**각각 매칭 시간 찾기**/
                    myMatchingAccount.setMatchingTime(request.getTime());
                    otherMatchingAccount.setMatchingTime(request.getTime());

                    //내가 남자라면
                    if(myMatchingAccount.getGender().equals(GenderEnum.남)){
                        request.setManId(myMatchingAccount.getId());
                        request.setWomanId(otherMatchingAccount.getId());
                    } else {
                        request.setManId(otherMatchingAccount.getId());
                        request.setWomanId(myMatchingAccount.getId());
                    }

                    matchingApiLogicService.create(request);

                    //TODO: 타임 내리는 쓰레드 생성
                    TimerTask timerTask = new TimerTask() {

                        /** 1. 랜덤값(분) 받아서 **/
                        int randomMin = random();
                        MatchingAccount my = matchingRoom.get(session.getId());
                        MatchingAccount peer = matchingRoom.get(my.getPeerSessionId());

                        @Override
                        public void run() {
                            if(randomMin >= 0){ /**
                             2. 1씩 값 내리고 **/
                                randomMin --;
                            } else{ /** 0보다 작은경우 task 종료 **/
                                cancel();
                            }
                            try {
                                /** 2, 매칭된 사용자 두명에게 초단위로 메세지 보내기 **/
                                sendMessage(my.getMySession(),new WebSocketMessage(my.getMySession().getId(),"timer",null,randomMin--));
                                sendMessage(peer.getMySession(),new WebSocketMessage(peer.getMySession().getId(),"timer",null,randomMin--));
                            } catch (Exception e) {
                                //TODO: 로그 필요
                            }

                        }
                    };

                    Timer timer = new Timer(true);
                    //지정한 시간(firstTime)부터 1초 간격(period) 으로 지정한 작업(task)을 수행한다.
                    timer.scheduleAtFixedRate(timerTask,0,1*1000);
                }
                break;


                /**수락 요청시**/
            case "accept":

                /**1. 수락을 보낸 사용자**/
                myMatchingAccount = matchingRoom.get(session.getId());
                otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());

                /** 2.매칭 테이블에서 자신의 이메일에 해당하는 record 받아와서 **/
                MatchingApiResponse matching = null;
                if(myMatchingAccount.getGender().equals(GenderEnum.남)){
                    MatchingApiRequest matchingApiRequest = MatchingApiRequest.builder()
                            .time(myMatchingAccount.getMatchingTime())
                            .manId(myMatchingAccount.getId())
                            .womanId(otherMatchingAccount.getId())
                            .build();
                    matching = matchingApiLogicService.findByManIdAndWomanIdAndTime(matchingApiRequest).getValue();
                } else{
                    MatchingApiRequest matchingApiRequest = MatchingApiRequest.builder()
                            .time(myMatchingAccount.getMatchingTime())
                            .manId(otherMatchingAccount.getId())
                            .womanId(myMatchingAccount.getId())
                            .build();
                    matching = matchingApiLogicService.findByManIdAndWomanIdAndTime(matchingApiRequest).getValue();
                }

                if(matching == null){
                    break;
                    //TODO : 로그추가필요
                }

                /**3. 자신이 남자이면 남자 state 변경, 여자이면 여자 state 변경**/
                if(myMatchingAccount.getGender().equals(GenderEnum.남)){
                    matching.setManUserState(1);
                } else {
                    matching.setWomanUserState(1);
                }

                /**매칭 룸 업데이트**/
                MatchingApiRequest matchingApiRequest = MatchingApiRequest.builder()
                        .id(matching.getId())
                        .manUserState(matching.getManUserState())
                        .womanUserState(matching.getWomanUserState())
                        .time(matching.getTime())
                        .manId(matching.getId())
                        .womanId(matching.getId())
                        .build();
                matchingApiLogicService.update(matchingApiRequest);

                /**4. 수락상태 확인 - 둘다 수락 상태이면 채팅 룸 생성**/
                if(matching.getWomanUserState()==1 && matching.getManUserState() == 1){

                    otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());

                    ChatroomApiRequest request = ChatroomApiRequest.builder()
                            .time(LocalDateTime.now().withNano(0))
                            .name(otherMatchingAccount.getNickName()) //TODO : 채팅방 제목 설정 논의필요
                            .build();
                    /**내가 남자라면**/
                    if(myMatchingAccount.getGender().equals(GenderEnum.남)){
                        request.setManId(myMatchingAccount.getId());
                        request.setWomanId(otherMatchingAccount.getId());
                    } else { /**내가 여자라면**/
                        request.setWomanId(myMatchingAccount.getId());
                        request.setManId(otherMatchingAccount.getId());
                    }

                    chatroomApiLogicService.create(request);

                    sendMessage(myMatchingAccount.getMySession(),new WebSocketMessage(myMatchingAccount.getMySession().getId(),"matching",null,otherMatchingAccount.getNickName()));
                    sendMessage(otherMatchingAccount.getMySession(),new WebSocketMessage(otherMatchingAccount.getMySession().getId(),"matching",null,myMatchingAccount.getNickName()));
                }

                /**수락 안했으므로 패스**/
                break;


                //TODO : 매칭이 종료된 경우 - 한쪽이 거절하기 누른경우
            case "disconnect" :
                myMatchingAccount = matchingRoom.get(session.getId());
                otherMatchingAccount = matchingRoom.get(myMatchingAccount.getPeerSessionId());

                /**대기룸 제거**/
                matchingRoom.remove(myMatchingAccount.getMySession().getId());
                matchingRoom.remove(otherMatchingAccount.getMySession().getId());
                /**세션 제거**/
                sessions.remove(myMatchingAccount.getMySession().getId());
                sessions.remove(otherMatchingAccount.getMySession().getId());
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

        //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
        if(matchingRoom.size() >= 0){
            for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
            }
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
        WebSocketSession s = sessions.get(session.getId());
        if(s != null){
            sessions.remove(session.getId());
        }

        /**종료한 Session이 대기룸에 있으면 제거**/
        if(matchingRoom.get(session.getId()) != null){
//            MatchingAccount my = matchingRoom.get(session.getId());
            matchingRoom.remove(session.getId());
        }

        //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
        if(matchingRoom.size() >= 0){
            for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
            }
        }
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


    /** 내가 원하는 학년이 상대방과 같은지 **/
    private boolean compareToWantGrade(MatchingAccount my, MatchingAccount other){
        return my.getWantGrade() == other.getGrade();
    }

    /** 나의 majorState가 0이 아닌 상황에서 비교**/
    private boolean compareToMajor(MatchingAccount my, MatchingAccount other){

        /** 나의 state 가 1이면 나와 상대방이 원하는 상대인지 비교 **/
        if(my.getMajorState() == 1){
            return my.getSelectMajor() == other.getMajorName();
        }
        else if(my.getMajorState() == 2){
            return my.getSelectMajor() != other.getMajorName();
        }
        return false;
    }
}



