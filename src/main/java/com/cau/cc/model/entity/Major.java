package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Accessors(chain = true)
@ToString(exclude = {"userId"})
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MajorEnum majorName; // 학과 [BIOLOGY, COMPSCI, ELECENG, FINANCE, HISTORY, MUSIC, PHYSICS]

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "majorId")
    private List<Account> accountList = new ArrayList<>();

}
