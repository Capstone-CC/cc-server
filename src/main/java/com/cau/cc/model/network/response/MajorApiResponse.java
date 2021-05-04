package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorApiResponse {

    private Long id;

    private MajorEnum majorName;

    private List<MajorEnum> majorEnums = new ArrayList<>();
}
