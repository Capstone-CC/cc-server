package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.ReportEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ReportApiResponse {

    private Long id;

    private ReportEnum contents;

    private LocalDateTime reportTime;

    private Long reporterId;

    private Long reportedId;

}
