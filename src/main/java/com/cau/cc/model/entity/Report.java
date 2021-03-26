package com.cau.cc.model.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = {"reporterId","reported_id"})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @JoinColumn(name = "reporter_id")
    @ManyToOne
    private Account reporterId;

    @JoinColumn(name = "reported_id")
    @ManyToOne
    private Account reportedId;

}
