package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(exclude = {"userId","chatroomId"})
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chatroom_id")
    @ManyToOne
    private Chatroom chatroomId;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private Account userId;

    @Enumerated(EnumType.STRING)
    private MessageType type; // 메시지 타입

    private String message; // 메시지
    private LocalDateTime time;
}