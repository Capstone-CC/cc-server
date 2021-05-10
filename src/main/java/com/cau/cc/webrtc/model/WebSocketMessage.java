package com.cau.cc.webrtc.model;

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
    private String grade;
    private String major;
    private Object data;
}
