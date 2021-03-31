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
@ToString(exclude = {"userId"})
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //enum
    private String majorName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "majorId")
    private List<Account> accountList = new ArrayList<>();

}
