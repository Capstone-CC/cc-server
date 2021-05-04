package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Accessors(chain = true)
@ToString(exclude = {"manList","womanList","manList_chat","womanList_chat","reporterList","reportedList"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String nickName;

    private String password;

    private String image;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender; // 성별 [남, 녀]

    private int age;

    private int grade; // 학년

    private int height;

    private String location;

    private String timeTable;

    private String content;

    private String email;

    private String verificationCode;

    private int count;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manId")
    private List<Matching> manList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "womanId")
    private List<Matching> womanList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manId")
    private List<Chatroom> manList_chat = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "womanId")
    private List<Chatroom> womanList_chat = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reporterId")
    private List<Report> reporterList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reportedId")
    private List<Report> reportedList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MajorEnum majorName; // 학과
/*
    @JoinColumn(name = "major_id")
    @ManyToOne
    private Major majorId;
*/

}
