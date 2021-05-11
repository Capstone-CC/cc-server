package com.cau.cc.webrtc.model;

import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessage {
    private String id;
    private String event;
    private Option option;
    private Object data; // 현재 offer 사용자
}



