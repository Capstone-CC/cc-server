package com.cau.cc.webrtc.model;

import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MatchingAccount {
    private int grade; // 학년
    private String email;
    private MajorEnum majorName;

    //매칭된 상대방 sessionId
    private String peerSessionId;

    //매칭상태
    private boolean matchingState;

    //수락상태
    private boolean acceptState;
}
