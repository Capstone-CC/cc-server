
package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = {"manId","womanId", "chatMessageList"})
@Accessors(chain = true)
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime time;

    @JoinColumn(name = "man_id")
    @ManyToOne
    private Account manId;

    @JoinColumn(name = "woman_id")
    @ManyToOne
    private Account womanId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatroomId")
    private List<ChatMessage> chatMessageList = new ArrayList<>();

}
