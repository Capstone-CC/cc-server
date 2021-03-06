package com.cau.cc.model.network.request;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountApiRequest {

    private String nickName;

    private String image;

    private GenderEnum gender;

    private MajorEnum majorName;

    private int grade; // 학년

    private String content;

    private String email;

    private String verificationCode;

    private boolean checkEmaile;

    private String password;

    private String confirmPw;

}
