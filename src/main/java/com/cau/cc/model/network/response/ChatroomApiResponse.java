package com.cau.cc.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatroomApiResponse {

    private Long id;

    private String name;

    private Long manId;

    private Long womanId;
}
