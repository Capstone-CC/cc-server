package com.cau.cc.model.entity;

import lombok.*;

import javax.persistence.*;

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

    @JoinColumn(name = "user_id")
    @ManyToOne
    private Account userId;

}
