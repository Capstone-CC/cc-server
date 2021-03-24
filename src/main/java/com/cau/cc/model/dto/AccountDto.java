package com.cau.cc.model.dto;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;

    private String name;

    private String nickName;

    private String password;

    private String image;

    private String gender;

    private int age;

    private int grade; // 학년

    private int height;

    private String location;

    private String timeTable;

    private String hobby;

    private String commend;

    private String email;

    private String verificationCode;

}
