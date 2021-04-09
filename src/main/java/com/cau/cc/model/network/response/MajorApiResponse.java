package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorApiResponse {

    private Long id;

    private MajorEnum majorName;
}
