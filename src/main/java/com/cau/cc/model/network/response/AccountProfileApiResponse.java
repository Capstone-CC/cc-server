package com.cau.cc.model.network.response;

import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AccountProfileApiResponse {

    private String email;

    private String image;

    private GenderEnum gender;

    private MajorEnum majorName;

    private int grade; // 학년

    private String nickName;

    private String content;

    private List<Chatroom> chatroomApiResponseList;

}