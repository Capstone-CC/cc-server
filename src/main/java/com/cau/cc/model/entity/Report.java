package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Accessors(chain = true)
@ToString(exclude = {"reporterId","reported_id"})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime reportTime;

    @JoinColumn(name = "reporter_id")
    @ManyToOne
    private Account reporterId;

    @JoinColumn(name = "reported_id")
    @ManyToOne
    private Account reportedId;

}
