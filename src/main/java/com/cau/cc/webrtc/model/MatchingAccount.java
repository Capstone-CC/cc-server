package com.cau.cc.webrtc.model;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MatchingAccount {

    private Long id;
    private WebSocketSession mySession;
    private int grade; // 학년
    private String email;
    private MajorEnum majorName;
    private GenderEnum gender;
    private int count;
    private String nickName;
    private long startTime;

    //빠른 타이머 종료를 위한 timerState 추가
    private boolean timerState; // 탐색 Thread -> 기본값 true로 시작 (false이면 탐색 종료)
    private boolean timeTimerState; // 매칭timer Thread -> 기본값 false로 시작 (매칭성공시 true, false이면 타이머종료)

    //DelayCount를 위한 정보
    private List<DelayObject> delayObjects = new ArrayList<>();

    //원하는 상대방 정보
    private MajorEnum selectMajor;
    private int majorState;
    private int gradeState;
    private int selectGrade;

    //매칭된 상대방 sessionId, userId, 매칭된 시간
    private String peerSessionId; //find에서 알고리즘 매칭시 저장됨
    private Long peerId; //find에서 알고리즘 매칭시 저장됨
    private LocalDateTime matchingTime; //매칭룸 만들때 저장됨

    //매칭상태
    private boolean matchingState; //내가 connect 보내기만 해도 true
    private boolean matchingfinalState; //둘다 성공한경우 true

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchingAccount that = (MatchingAccount) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
