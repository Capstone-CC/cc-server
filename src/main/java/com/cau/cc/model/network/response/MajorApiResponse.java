package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.MajorEnum;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private MajorEnum majorName;

    private List<MajorEnum> majorEnums = new ArrayList<>();
}
