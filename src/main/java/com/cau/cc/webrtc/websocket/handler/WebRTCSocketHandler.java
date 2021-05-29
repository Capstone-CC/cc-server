package com.cau.cc.webrtc.websocket.handler;

import com.amazonaws.transform.MapEntry;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.entity.ReportEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.request.MatchingApiRequest;
import com.cau.cc.model.network.request.ReportApiRequest;
import com.cau.cc.model.network.response.MatchingApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.service.ChatroomApiLogicService;
import com.cau.cc.service.MatchingApiLogicService;
import com.cau.cc.service.ReportApiLogicService;
import com.cau.cc.webrtc.model.DelayObject;
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

    /**매칭이 Connect가 된 사용자가 있는방**/
    /**session ID - MatchingAccount**/
    public static Map<String,MatchingAccount> connectRoom = new ConcurrentHashMap<>();

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper mapper = new ObjectMapper();

    private MatchingApiLogicService matchingApiLogicService;

    private AccountRepository accountRepository;

    private ChatroomApiLogicService chatroomApiLogicService;

    private ReportApiLogicService reportApiLogicService;

    // 3분, 5분, 7분, 10분을 초단위로
    private int[] randomNum = {180,300,420,600};


    @Autowired
    public WebRTCSocketHandler(MatchingApiLogicService matchingApiLogicService,
                               AccountRepository accountRepository,
                               ChatroomApiLogicService chatroomApiLogicService,
                               ReportApiLogicService reportApiLogicService) {
        this.matchingApiLogicService = matchingApiLogicService;
        this.accountRepository = accountRepository;
        this.chatroomApiLogicService = chatroomApiLogicService;
        this.reportApiLogicService = reportApiLogicService;

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
                            .myMessage(webSocketMessage)
                            .startTime(System.currentTimeMillis())
                            .delayObjects(matchingApiLogicService.findById(account.getGender(),account.getId()))
                            .selectGrade(webSocketMessage.getOption().getGrade())
                            .gradeState(webSocketMessage.getOption().getGradeState())
                            .selectMajor(webSocketMessage.getOption().getMajorName())
                            .majorState(webSocketMessage.getOption().getMajorState())
                            .build();
                    matchingRoom.put(session.getId(),myMatchingAccount);

                    /**매칭룸 자신을 제외한 상대방에게 모두 message보내기 들어갔으므로 **/
                    //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
                    if(matchingRoom.size() >= 0){
                        for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                            if(!myMatchingAccount.getMySession().getId().equals(current.getKey())){
                                sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
                            }
                        }
                    }
                }

                //TODO: 매칭 시도하는 쓰레드 생성
                TimerTask matchingThread = new TimerTask() {
                    /** Task 실행시 session에서 값 꺼내 my를 고정시켜준다!!! **/
                    MatchingAccount my = matchingRoom.get(session.getId());
                    MatchingAccount peer = null;

                    @Override
                    public void run() {

                        if(my == null){
                            cancel();
                        }
                        //TODO : 찾는중 (10초마다 run이 실행되므로 10초마다 보냄)
                        sendMessage(my.getMySession(), new WebSocketMessage(my.getMySession().getId(),"searching",null,null));


                        /** 자신이 매칭 된 상태이면 STOP**/
                        if(my.isMatchingState()){
                            matchingRoom.remove(my.getMySession().getId());
                            cancel();
                        }

                        long currnetTime = (System.currentTimeMillis()-my.getStartTime())/1000;
                        /**1분동안 못찾았으면 메세지 보내고 cancel()**/
                        if((currnetTime > 60)){
                            matchingRoom.remove(my.getMySession().getId());
                            sendMessage(my.getMySession(), new WebSocketMessage(my.getMySession().getId(),"notfound",null,null));
                            cancel();
                        }

                        //매칭상대 찾은 상태값 0: 몾찾음, 1: 찾음
                        int start = 0;

                        //매칭 기록 있는지 확인
                        DelayObject delayPeerObject = new DelayObject();

                        /**
                         * 매칭 알고리즘
                         */
                        /** 1. 전체 웹소켓 세션을 돌면서 **/
                        for( Map.Entry<String,WebSocketSession> otherSession : sessions.entrySet()){

                            start = 1;

                            /**2. 대기룸에 있는 사용자 중에서 자신이 아닌 상대방 찾고**/
                            peer = matchingRoom.get(otherSession.getKey());
                            /**if 체크 순서 중요! **/
                            if( peer != null && !my.getMySession().getId().equals(peer.getMySession().getId())) {

                                //나와 연결되었던 상대의 아이디를 찾을 임시 객체
                                delayPeerObject.setId(peer.getId());

                                /**
                                 * Delay는 내가 이전에 매칭된 상대방을 찾는경우 나와 상대방 count 모두 +1 된다
                                 * 즉 매칭되었던 2명의 사용자가 반복적으로 3번 매칭 되는 경우만 매칭된다.
                                 */
                                /** 이전에 매칭된 사람 만나면 delayCount++ 하는데 3이상이면 그냥 매칭 **/
                                if(my.getDelayObjects().contains(delayPeerObject)){//peer가 자신과 이전에 매칭 된 사람이고
                                    //나와 연결되었던 상대의 아이디가 담긴 객체
                                    DelayObject mypeerDelayObject = my.getDelayObjects().get(my.getDelayObjects().indexOf(delayPeerObject));
                                    if(mypeerDelayObject.getDelayCount() <= 2){ //delayCount가 2이하라면
                                        /** 자신의 객체의 있는 해당 사람과의 delayCount ++ 하고 상대방도 ++ 한 후 continue **/
                                        mypeerDelayObject.setDelayCount(mypeerDelayObject.getDelayCount()+1);

                                        //상대방의 연결되었던 객체의 나의 아이디를 이용해서 찾고
                                        delayPeerObject.setId(my.getId());
                                        // 이를 통해 상대방의 매칭되었던(=my)의 아이디가 담긴 객체 가져와서 count++
                                        DelayObject peerRealDelayObject = peer.getDelayObjects().get(peer.getDelayObjects().indexOf(delayPeerObject));
                                        peerRealDelayObject.setDelayCount(peerRealDelayObject.getDelayCount()+1);
                                        start = 0;
                                        continue;
                                    }
                                }

                                /**3. 매칭된 상대방이 없거나 이미 매칭된 상태 또는 자신과 같은 성별이라면 continue**/
                                if(peer.isMatchingState()
                                        || my.getGender().equals(peer.getGender())){
                                    start = 0;
                                    continue;
                                }

                                /**4.매칭안된 사용자이고 자신과 다른 성별이면 조건의 맞는지 확인**/

                                /**gradeState가 0이 아니면 전체선택, Majorstate가 0이면 전체선택**/
                                if(my.getGradeState() != 0 && my.getMajorState() != 0){ // 학년, 학과모두 상관 있으면

                                    if(!compareToGrade(my,peer)
                                            || !compareToMajor(my, peer)){
                                        //둘중 하나라도 해당 안되면
                                        start = 0;
                                        continue;
                                    }

                                    //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                                    //상대방 기준
                                    if(peer.getGradeState() != 0 && peer.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                        if(!compareToGrade(peer, my)
                                                || !compareToMajor(peer, my)){
                                            //둘중 하나라도 해당 안되면
                                            start = 0;
                                            continue;
                                        }
                                    }
                                    else if(peer.getGradeState() == 0 && peer.getMajorState() != 0){//학년 상관X, 학과 상관O
                                        if(!compareToMajor(peer,my)){ //state에 따른 학과 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    } else if(peer.getGradeState() != 0 && peer.getMajorState() == 0){//학과 상관X, 학년 상관
                                        if (!compareToGrade(peer,my)) { //state에 따른 학년 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    }

                                    /**gradeState가 0으로 전체선택이고 Majorstate가 0이 아닌 상황**/
                                } else if(my.getGradeState() == 0 && my.getMajorState() != 0) { //학년 상관X, 학과 상관O
                                    if(!compareToMajor(my,peer)){ //state에 따른 학과 매칭 실패
                                        start = 0;
                                        continue;
                                    }

                                    //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                                    //상대방 기준
                                    if(peer.getGradeState() != 0 && peer.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                        if(!compareToGrade(peer, my)
                                                || !compareToMajor(peer, my)){
                                            //둘중 하나라도 해당 안되면
                                            start = 0;
                                            continue;
                                        }
                                    }
                                    else if(peer.getGradeState() == 0 && peer.getMajorState() != 0){//학년 상관X, 학과 상관O
                                        if(!compareToMajor(peer,my)){ //state에 따른 학과 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    } else if(peer.getGradeState() != 0 && peer.getMajorState() == 0){//학과 상관X, 학년 상관
                                        if (!compareToGrade(peer,my)) { //state에 따른 학년 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    }

                                    /**gradeState가 0 전체 선택 아니고 Majorstate가 0인 상황**/
                                } else if (my.getGradeState() != 0 && my.getMajorState() == 0) { //학과 상관X, 학년 상관
                                    /**state에 따른 학년 매칭 되는지 확인 **/
                                    if (!compareToGrade(my,peer)) { //state에 따른 학년 매칭 실패
                                        start = 0;
                                        continue;
                                    }

                                    //TODO : 나의 조건이 상대방에게 맞는 경우이므로 상대방의 조건이 나와 맞는지 확인
                                    //상대방 기준
                                    if(peer.getGradeState() != 0 && peer.getMajorState() != 0) { // 학년, 학과모두 상관 있으면
                                        if(!compareToGrade(peer, my)
                                                || !compareToMajor(peer, my)){
                                            //둘중 하나라도 해당 안되면
                                            start = 0;
                                            continue;
                                        }
                                    }
                                    else if(peer.getGradeState() == 0 && peer.getMajorState() != 0){//학년 상관X, 학과 상관O
                                        if(!compareToMajor(peer,my)){ //state에 따른 학과 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    } else if(peer.getGradeState() != 0 && peer.getMajorState() == 0){//학과 상관X, 학년 상관
                                        if (!compareToGrade(peer,my)) { //state에 따른 학년 매칭 실패
                                            start = 0;
                                            continue;
                                        }
                                    }

                                }

                                /**조건 일치**/
                                if(start == 1){
                                    //바로 메시지 보내기
                                    /**5. 조건의 맞다면 각자 자신의 상대 sessionid 저장후**/
                                    my.setPeerSessionId(peer.getMySession().getId()); // 나의 객체의 상대방 id 저장
                                    peer.setPeerSessionId(my.getMySession().getId()); //상대방 객체의 나의 id 저장

                                    /**6. 자신의 offer 보내기 **/
                                    sendMessage(peer.getMySession(), my.getMyMessage());
                                    cancel();
                                }
                            }
                        }//end for
                    }//end run
                };

                Timer matchingTimer = new Timer(true);
                //지정한 시간(firstTime)부터 10초 간격(period) 으로 지정한 작업(task)을 수행한다.
                matchingTimer.scheduleAtFixedRate(matchingThread,0,10*1000);
                break;

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

                    /**연결되었으므로 각각 상대방의 id를 저장**/
                    myMatchingAccount.setPeerId(otherMatchingAccount.getId());
                    otherMatchingAccount.setPeerId(myMatchingAccount.getId());

                    /**각각 대기룸에서 나오기**/
                    matchingRoom.remove(myMatchingAccount.getMySession().getId());
                    matchingRoom.remove(otherMatchingAccount.getMySession().getId());

                    /**매칭룸 자신과 상대방을 제외한 사람들에게 모두 message보내기 들어갔으므로 **/
                    //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
                    if(matchingRoom.size() >= 0){
                        for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                            if(!myMatchingAccount.getMySession().getId().equals(current.getKey())
                            && !otherMatchingAccount.getMySession().getId().equals(current.getKey())){
                                sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
                            }
                        }
                    }

                    /**연결 방으로 들어가기**/
                    connectRoom.put(myMatchingAccount.getMySession().getId(),myMatchingAccount);
                    connectRoom.put(otherMatchingAccount.getMySession().getId(),otherMatchingAccount);

                    /**각각 count 값 1씩 감소**/
                    Account tmp = accountRepository.findByEmail(myMatchingAccount.getEmail());
                    int myCount = tmp.getCount()-1;
                    tmp.setCount(myCount);
                    accountRepository.save(tmp);
                    Account tmp2 = accountRepository.findByEmail(otherMatchingAccount.getEmail());
                    int otherCount = tmp2.getCount()-1;
                    tmp2.setCount(otherCount);
                    accountRepository.save(tmp2);

                    /**각각 감소된 카운트 보내주기**/
                    sendMessage(myMatchingAccount.getMySession(),
                            new WebSocketMessage(myMatchingAccount.getMySession().getId(),"ticket",null,myCount));
                    sendMessage(otherMatchingAccount.getMySession(),
                            new WebSocketMessage(otherMatchingAccount.getMySession().getId(),"ticket",null,otherCount));


                    /**각 객체의 감소된 Count 저장**/
                    myMatchingAccount.setCount(myMatchingAccount.getCount()-1);
                    otherMatchingAccount.setCount(otherMatchingAccount.getCount()-1);

                    /**서로 매칭 DelayObject의 넣기**/
                    DelayObject delayPeerObject = new DelayObject(otherMatchingAccount.getId(),0);
                    if(!myMatchingAccount.getDelayObjects().contains(delayPeerObject)) {
                        /**peer가 자신과 이전에 매칭 된 사람이 아니면서로 추가하기 **/
                        myMatchingAccount.getDelayObjects().add(new DelayObject(otherMatchingAccount.getId(),0));
                        otherMatchingAccount.getDelayObjects().add(new DelayObject(myMatchingAccount.getId(), 0));
                        delayPeerObject = null; //사용 안하는 객체명시
                    }

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
                        MatchingAccount my = connectRoom.get(session.getId());
                        MatchingAccount peer = connectRoom.get(my.getPeerSessionId());

                        @Override
                        public void run() {
                            if(randomMin >= 0 && connectRoom.get(my.getMySession().getId()) != null
                                    && connectRoom.get(peer.getMySession().getId()) != null){
                                /** 2. 1씩 값 내리고 **/
                                randomMin --;
                            } else{ /** 0보다 작거나 세션에 없는 경우 task 종료 **/
                                cancel();
                            }
                            try {
                                /** 2, 매칭된 사용자 두명에게 초단위로 메세지 보내기 **/
                                sendMessage(my.getMySession(),new WebSocketMessage(my.getMySession().getId(),"timer",null,randomMin));
                                sendMessage(peer.getMySession(),new WebSocketMessage(peer.getMySession().getId(),"timer",null,randomMin));
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
                myMatchingAccount = connectRoom.get(session.getId());
                otherMatchingAccount = connectRoom.get(myMatchingAccount.getPeerSessionId());

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

                    otherMatchingAccount = connectRoom.get(myMatchingAccount.getPeerSessionId());

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

                //TODO : 매칭 시도 후 취소하는 경우
            case "cancel":
                try{
                    /**1. 취소를 보낸 사용자와 상대 사용자 꺼내서**/
                    myMatchingAccount = matchingRoom.get(session.getId());

                    /**2. 매칭 취소 했으므로 대기방에서 지우기**/
                    matchingRoom.remove(myMatchingAccount.getMySession().getId());

                    /**매칭룸 인원 변경되었으므로 모두 message보내기 **/
                    //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
                    if(matchingRoom.size() >= 0){
                        for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                            sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
                        }
                    }

                    /**3. 상태 변경**/
                    myMatchingAccount.setMatchingState(false);
                    otherMatchingAccount.setMatchingState(false);
                } catch (Exception e){
                    //TODO : 로그추가
                }
                break;

                //TODO : 매칭이 종료된 경우 - 한쪽이 거절하기 누른경우
            case "disconnect" :
                /** 자신이 매칭룸에 없으면 상대방이 먼저 취소한 경우 이므로 체크**/
                try{
                    myMatchingAccount = connectRoom.get(session.getId());
                    if(myMatchingAccount != null){
                        connectRoom.remove(myMatchingAccount.getMySession().getId());
                        connectRoom.remove(myMatchingAccount.getPeerSessionId());
                    }
                }catch (Exception e){
                    //TODO : 로그필요
                }
                break;

                //TODO : 매칭된 상태에서 신고
            case "report":
                myMatchingAccount = connectRoom.get(session.getId());
                if(myMatchingAccount == null){
                    myMatchingAccount = matchingRoom.get(session.getId());
                }
                ReportEnum reportMsg = ReportEnum.valueOf((String)(webSocketMessage.getData()));

                //신고서 작성
                ReportApiRequest request = ReportApiRequest.builder()
                        .contents(reportMsg)
                        .reporterId(myMatchingAccount.getId())
                        .reportedId(myMatchingAccount.getPeerId())
                        .build();

                reportApiLogicService.create(request);
                break;
        }
    }

    /**
     * 모든 클라이언트를 추적 할 수 있도록 수신 된 세션을 세션 목록에 추가
     * 클라이언트가 socket 요청 보내는데 그 때 해당 메소드 실행됨
     * 즉, 클라이언트가 socket 요청 -> 해당메소드 실행
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

        /**웹소켓 연결된 사용자에게 자신의 남은 count값 보내주기**/
        Account account = null;
        int countNum = 0;
        try{
            Authentication authentication = (Authentication) session.getPrincipal();
            account = (Account) authentication.getPrincipal();
            account = accountRepository.findByEmail(account.getEmail());
            countNum = account.getCount();
        }catch (Exception e){
            synchronized( sessions ) {
                /**없으면 쿠기없다는 메세지 보내기**/
                sendMessage(session,new WebSocketMessage(session.getId(),"notcookie",null,null));
                sessions.remove( session );
            }
        }

        sendMessage(session,new WebSocketMessage(session.getId(),"ticket",null,countNum));
        account = null; //사용 안하는 객체명시



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
     * or 소켓 에러 발생시 실행
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

        /**종료한 Session이 매칭룸에 있으면 제거**/
        if(connectRoom.get(session.getId()) != null){
//            MatchingAccount my = matchingRoom.get(session.getId());
            connectRoom.remove(session.getId());
        }

        //TODO : 현재 계속 1 Client에게 2번씩 보내는 문제 해결필요
        if(matchingRoom.size() >= 0){
            for( Map.Entry<String,WebSocketSession> current : sessions.entrySet()){
                sendMessage(current.getValue(),new WebSocketMessage(current.getKey(),"client",null,matchingRoom.size()));
            }
        }
    }

    /**메시지 보내기**/
    private synchronized void sendMessage(WebSocketSession webSocketSession, WebSocketMessage webSocketMessage) {
        try {
            if(webSocketSession.isOpen()){
                String json = mapper.writeValueAsString(webSocketMessage);
                webSocketSession.sendMessage(new TextMessage(json));
            }
        } catch (IOException e) {
            //TODO : 로그 필요
        }
    }

    /** 나의 gradeState가 0이 아닌 상황에서 비교**/
    private boolean compareToGrade(MatchingAccount my, MatchingAccount other){
        
        /** 나의 state 가 1이면 상대방이 내가 매칭되고 싶은 grade가 맞는지 **/
        if(my.getGradeState() == 1){
            return my.getSelectGrade() == other.getGrade();
        }
        /** 나의 state가 2이면 상대방이 내가 매칭되기 싫은 grade가 맞는지**/
        else if(my.getGradeState() == 2){
            return my.getSelectGrade() != other.getGrade();
        }
        return false;
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



