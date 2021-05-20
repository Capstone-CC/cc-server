package com.cau.cc.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatroomApiRequest {

    private Long id;

    private String name;

    private Long manId;

    private Long womanId;

    private LocalDateTime time;
}
