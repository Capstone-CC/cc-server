package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.request.AccountProfileApiRequest;
import com.cau.cc.model.network.response.*;
import com.cau.cc.service.AccountProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "프로필 Read & Update")
@RequestMapping("/profile")
public class AccountProfileApiController {

    @Autowired
    private AccountProfileService accountProfileService;

    @PostMapping("") // /api/profile
    public Header<AccountProfileApiResponse> create(@RequestBody AccountProfileApiRequest request) {
        return accountProfileService.create(request);
    }

    @GetMapping("")
    @ApiOperation(value = "프로필 Read",notes = "프로필 Read")
    public Header<AccountProfileApiResponse> read() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) auth.getPrincipal();

        if(account.getEmail() == null) {
            return Header.ERROR("로그인이 필요합니다.");
        }
        return accountProfileService.read(account.getEmail());
    }

    @PutMapping("") // /api/profile
    @ApiOperation(value = "프로필 Update",notes = "프로필 Update")
    public Header<AccountProfileApiResponse> update(@RequestBody AccountProfileApiRequest request) {
        return accountProfileService.update(request);
    }

    @DeleteMapping("{id}") // /api/profile/id
    public Header delete(@PathVariable Long id) {
        return accountProfileService.delete(id);
    }

    /**
     * chat list api
     */
    @GetMapping("/chat/list")
    public Header<AccountChatListApiResponse> chatInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) auth.getPrincipal();

        return accountProfileService.chatInfo(account.getEmail());
    }

    /**
     * chat room 클릭시 저장된 db page로 불러오기.
     */
    @GetMapping("/chat/list/{id}")
    public Header<List<ChatMessageApiResponse>> search(@PathVariable Long id,@PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return accountProfileService.search(id, pageable);
    }


}
