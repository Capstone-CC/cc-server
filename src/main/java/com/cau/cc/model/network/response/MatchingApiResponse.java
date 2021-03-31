package com.cau.cc.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingApiResponse {

    private Long id;

    private int manUserState;

    private int womanUserState;

    private LocalDateTime time;

    private Long manId;

    private Long womanId;

}
