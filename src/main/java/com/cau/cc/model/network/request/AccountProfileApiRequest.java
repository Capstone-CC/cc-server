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
public class AccountProfileApiRequest {

    @ApiModelProperty(example = "푸앙이")
    private String nickName;

    @ApiModelProperty(example = "이미지경로")
    private String image;

    @ApiModelProperty(example = "남")
    private GenderEnum gender;

    @ApiModelProperty(example = "소프트웨어대학")
    private MajorEnum majorName;

    @ApiModelProperty(example = "2")
    private int grade; // 학년

    @ApiModelProperty(example = "자기소개")
    private String content;

    @ApiModelProperty(example = "test")
    private String email;


}
