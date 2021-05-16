package com.cau.cc.chat.websocket;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatAccount {

    private Long id;
    private WebSocketSession mySession;
    private int grade; // 학년
    private String email;
    private MajorEnum majorName;
    private GenderEnum gender;
    private int count;
    private String nickName;

    //원하는 상대방 정보
    private MajorEnum wantMajor;
    private int wantGrade;

    //매칭된 상대방 sessionId, 매칭된 시간
    private String peerSessionId;
    private LocalDateTime matchingTime;

    //매칭상태
    private boolean matchingState;

}
