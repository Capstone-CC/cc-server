package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.response.AccountChatListApiResponse;
import com.cau.cc.model.network.response.AccountOtherResponse;
import com.cau.cc.model.network.response.ChatMessageApiResponse;
import com.cau.cc.model.repository.ChatMessageRepository;
import com.cau.cc.service.AccountProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
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

    @Autowired
    ChatMessageRepository chatMessageRepository;

    /**
     * chat list api
     */
    @ApiOperation(value = "채팅 목록",notes = "status : 0 이면 보이고 status : 1 채팅방 나감")
    @GetMapping("/list")
    public Header<AccountChatListApiResponse> chatInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) auth.getPrincipal();

        return accountProfileService.chatInfo(account.getEmail());
    }

    /**
     * chat room 클릭시 저장된 db page로 불러오기.
     */
    @ApiOperation(value = "채팅 내용",notes = "필수정보 : /list/roomId?page=? ?는 0부터 시작")
    @GetMapping("/list/{id}")
    public Header<List<ChatMessageApiResponse>> search(@PathVariable Long id,
                                                       @ApiIgnore @PageableDefault(sort = "time",
                                                               direction = Sort.Direction.DESC, size = 30) Pageable pageable) {
        return accountProfileService.search(id, pageable);
    }

//    @GetMapping("/list/{roomid}")
//    public Header<List<ChatMessageApiResponse>> page(@PathVariable Long roomid,
//                                                       @RequestParam("page") Long page) {
//        //return accountProfileService.page(roomid, page);
//    }


    @ApiOperation(value = "채팅 삭제",notes = "필수정보 : roomId 값")
    @DeleteMapping("/list/{id}")
    public Header delete(@PathVariable Long id) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Account account = (Account) auth.getPrincipal();

            return accountProfileService.delete(account.getEmail(), id);

        }catch (Exception e){
            return Header.ERROR("로그인 필요");
        }

    }

    @ApiOperation(value = "채팅 상대 프로필",notes = "id = chatroomId")
    @GetMapping("/list/{id}/other")
    public Header<AccountOtherResponse> other(@PathVariable Long id) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Account account = (Account) auth.getPrincipal();

            return accountProfileService.other(account.getEmail(), id);

        }catch (Exception e){
            return Header.ERROR("로그인 필요");
        }
    }

}
