package com.cau.cc.model.network.request;

import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorApiRequest {

    private Long id;

    private MajorEnum majorName;
}
