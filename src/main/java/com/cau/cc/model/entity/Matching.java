package com.cau.cc.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = {"manId","womanId","chatroomList"})
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int manUserState;

    private int womanUserState;

    private String time;

    @JoinColumn(name = "man_id")
    @ManyToOne
    private Account manId;

    @JoinColumn(name = "woman_id")
    @ManyToOne
    private Account womanId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchingId")
    private List<Chatroom> chatroomList = new ArrayList<>();


}
