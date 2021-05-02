package com.cau.cc.model.network.request;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountApiRequest {
    private Long id;

    private String name;

    private String nickName;

    private String password;

    private String confirmPw;

    private String image;

    private GenderEnum gender;

    private MajorEnum majorName;

    private int age;

    private int grade; // 학년

    private int height;

    private String location;

    private String timeTable;

    private String content;

    private String email;

    private String verificationCode;

    private boolean checkEmaile;

}
