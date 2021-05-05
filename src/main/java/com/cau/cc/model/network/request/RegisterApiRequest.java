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
public class RegisterApiRequest {


    @ApiModelProperty(example = "남")
    private GenderEnum gender;

    @ApiModelProperty(example = "소프트웨어대학")
    private MajorEnum majorName;

    @ApiModelProperty(example = "1")
    private int grade; // 학년

    @ApiModelProperty(example = "test")
    private String email;

    @ApiModelProperty(example = "123123")
    private String password;

    @ApiModelProperty(example = "123123")
    private String confirmPw;
}
