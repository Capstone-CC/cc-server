package com.cau.cc.webrtc.model;

import com.cau.cc.model.entity.MajorEnum;
import lombok.Data;

@Data
public class Option {
    private int grade;
    private MajorEnum majorName;
    private int majorState; //0: All , 1: like, 2 : unlike
    private int gradeState; //0: All , 1: like, 2 : unlike
}
