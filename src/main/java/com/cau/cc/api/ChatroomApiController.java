package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.AccountChatListApiResponse;
import com.cau.cc.model.network.response.ChatMessageApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.service.AccountProfileService;
import com.cau.cc.service.ChatroomApiLogicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

@RestController
@Api(tags = "Chatting Room API")
@RequestMapping("/chatroom")
public class ChatroomApiController  {

    @Autowired
    AccountProfileService accountProfileService;

    /**
     * chat list api
     */
    @ApiOperation(value = "채팅 목록",notes = " ")
    @GetMapping("/list")
    public Header<AccountChatListApiResponse> chatInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) auth.getPrincipal();

        return accountProfileService.chatInfo(account.getEmail());
    }

    /**
     * chat room 클릭시 저장된 db page로 불러오기.
     */
    @ApiOperation(value = "채팅 내용",notes = "필수정보 : roomId 값")
    @GetMapping("/list/{id}")
    public Header<List<ChatMessageApiResponse>> search(@PathVariable Long id, @ApiIgnore @PageableDefault(sort = "time", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return accountProfileService.search(id, pageable);
    }

}
