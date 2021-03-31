package com.cau.cc.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountApiRequest {
    private Long id;

    private String name;

    private String nickName;

    private String password;

    private String image;

    private String gender;

    // TODO : major 추가

    private int age;

    private int grade; // 학년

    private int height;

    private String location;

    private String timeTable;

    private String hobby;

    private String commend;

    private String email;

    private String verificationCode;

    private boolean checkEmaile;

}
