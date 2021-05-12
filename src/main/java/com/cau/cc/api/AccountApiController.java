package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountModifyRequest;
import com.cau.cc.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Account API")
@RequestMapping("/account")
public class AccountApiController {

    @Autowired
    private AccountService accountService;

    /**
     * 비밀번호 변경
     */
    @ApiOperation(value = "비밀번호 변경",notes = "필수 정보 : 현재 비밀번호 , 변경 할 비밀번호, 변경 할 비밀번호 확인")
    @PutMapping("/password")
    public Header modify(@RequestBody AccountModifyRequest request){
        Account account = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
             account = (Account) auth.getPrincipal();
        }catch (Exception e){
            return Header.ERROR("로그인이 필요합니다.");
        }

        if(account.getEmail() == null) {
            return Header.ERROR("로그인이 필요합니다.");
        }

        request.setEmail(account.getEmail());

        /**로그인 된 사용자**/
        return accountService.modify(request);
    }

    /**
     * 비밀번호 찾기
     */
    @GetMapping("/find")
    @ApiOperation(value = "비밀번호 찾기-개발중",notes = "")
    public Header find(String email){
        return null;
    }



//    @Autowired
//    private AccountService accountService;
//
//
//
//    @Override // 호출안됨
//    public Header<AccountApiResponse> create(@RequestBody Header<AccountApiRequest> request) {
//        return null;
//    }
//
//    @Override
//    @GetMapping("")
//    public Header<AccountApiResponse> read(@RequestBody Long id) {
//    //    return accountService.read(id);
//    }
//
//    @Override
//    @PutMapping("")
//    public Header<AccountApiResponse> update(@RequestBody Header<AccountApiRequest> request) {
//        return accountService.update(request);
//    }
//
//    @Override
//    @DeleteMapping("")
//    public Header delete(@RequestBody Long id) {
//        return accountService.delete(id);
//    }
}
