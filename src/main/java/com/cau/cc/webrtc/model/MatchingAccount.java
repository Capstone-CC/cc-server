package com.cau.cc.webrtc.model;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

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

    //매칭된 상대방 sessionId
    private String peerSessionId;

    //매칭상태
    private boolean matchingState;

}
