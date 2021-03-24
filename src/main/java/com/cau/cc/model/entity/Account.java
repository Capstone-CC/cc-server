package com.cau.cc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Account> reporterList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Account> reportedList;
}
