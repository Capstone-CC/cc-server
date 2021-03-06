package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.MessageType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Example;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ChatMessageApiResponse {

    private Long id;

    @ApiModelProperty(example = "푸앙이")
    private String sender;

    private Long userId;

    private MessageType type; // 메시지 타입
    private String message; // 메시지
    private LocalDateTime time;

}
