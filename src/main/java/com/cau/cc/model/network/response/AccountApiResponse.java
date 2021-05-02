package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AccountApiResponse {

    private String email;

    private GenderEnum gender;

    private MajorEnum majorName;

    private int grade; // 학년

    private String nickName;

    private String content;

}
