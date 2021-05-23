package com.cau.cc.model.network.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountFindRequest {

    @ApiModelProperty(example = "test")
    private String email;

    @ApiModelProperty(example = "이메일에서 받은 비번")
    private String temporaryPw;

    @ApiModelProperty(example = "123123")
    private String changePw;

    @ApiModelProperty(example = "123123")
    private String confirmPw;

    @ApiModelProperty(hidden = true)
    private boolean state;
}
