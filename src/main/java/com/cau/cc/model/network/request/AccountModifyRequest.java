package com.cau.cc.model.network.request;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountModifyRequest {

    @ApiModelProperty(hidden = true)
    private String email;

    @ApiModelProperty(example = "originPw")
    private String originPw;

    @ApiModelProperty(example = "123123")
    private String password;

    @ApiModelProperty(example = "123123")
    private String confirmPw;
}