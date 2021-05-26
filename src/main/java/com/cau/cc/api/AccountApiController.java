package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountFindRequest;
import com.cau.cc.model.network.request.AccountModifyRequest;
import com.cau.cc.model.network.request.RegisterApiRequest;
import com.cau.cc.service.AccountService;
import com.cau.cc.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@RestController
@Api(tags = "Account API")
@RequestMapping("/account")
public class AccountApiController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    /**
     * 비밀번호 변경
     */
    @ApiOperation(value = "로그인 한 사용자 비밀번호 변경",notes = "필수 정보 : 현재 비밀번호 , 변경 할 비밀번호, 변경 할 비밀번호 확인")
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
    @GetMapping("/password/find")
    @ApiOperation(value = "비밀번호 찾기",notes = "")
    public Header find(@ApiParam(value = "찾을 계정 이메일" ,required = true, example = "test") @RequestParam String email,
                       @ApiIgnore() HttpSession httpSession){

        //TODO: 존재하는 계정인지
        if(accountService.emailCheck(email)){
           return Header.ERROR("존재하지 않는 email 입니다.");
        }

        //TODO : 임시비번 생성 후 이메일 보내고 세션에 받은 이메일은 key로 비번찾기 DTO 저장
        String temporaryPw = "";
        try {
            temporaryPw = emailService.sendVerificationPw(email);
        } catch (MessagingException e) {
            //TODO: 로그 필수
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            //TODO: 로그 필수
            e.printStackTrace();
        }

        AccountFindRequest request = AccountFindRequest.builder()
                .email(email)
                .temporaryPw(temporaryPw)
                .state(false)
                .build();

        //세션에 받은 이메일을 key로 AccountDTO 객체 Session의 저장
        //세션 만료 시간 3분(초단위이므로 180) - TEST 완료
        httpSession.setAttribute(email,request);
        httpSession.setMaxInactiveInterval(180);
        return Header.OK();
    }

    /**
     * 임시비밀번호 확인
     */
    @ApiOperation(value = "임시비밀번호 확인",notes = "")
    @GetMapping("/password/verify")
    public Header passwordVerify(@ApiParam(value = "이메일주소" ,required = true, example = "test") @RequestParam String email,
                                 @ApiParam(value = "이메일 임시비밀번호",required = true) @RequestParam String temporaryPw,
                                 @ApiIgnore() HttpSession httpSession){

        //파라미터로 받은 계정
        AccountFindRequest newRequest = AccountFindRequest.builder()
                .email(email)
                .temporaryPw(temporaryPw)
                .build();

        //원래 해당하는 세션에서 찾기
        AccountFindRequest originRequest = (AccountFindRequest) httpSession.getAttribute(email);

        //세션 만료
        if(originRequest == null){
            return Header.ERROR("세션 만료, 비밀번호 찾기를 다시 진행해주세요");
        }

        //임시비밀번호 비교
        if(!originRequest.getTemporaryPw().equals(newRequest.getTemporaryPw())){
            return Header.ERROR("임시비밀번호가 틀립니다.");
        }

        //맞으면
        originRequest.setState(true);
        return Header.OK();
    }

    @ApiOperation(value = "임시비밀번호 인증 후 비밀번호 변경",notes = "필수 정보 : 이메일, 변경 할 비밀번호, 변경 할 비밀번호 확인")
    @PutMapping("/password/modify")
    public Header passwordModify(@RequestBody AccountFindRequest request,
                                 @ApiIgnore() HttpSession httpSession){
        //원래 해당하는 세션에서 찾기
        AccountFindRequest originRequest = (AccountFindRequest) httpSession.getAttribute(request.getEmail());

        //세션 만료
        if(originRequest == null){
            return Header.ERROR("세션 만료, 비밀번호 찾기를 다시 진행해주세요");
        }

        //임시비밀번호가 맞는 사용자 인지 검토
        if(!originRequest.isState()){
            return Header.ERROR("임시비밀번호를 인증해주세요");
        }

        //인증된 사용자 이므로 세션에서 지우고
        httpSession.removeAttribute(request.getEmail());

        //비밀번호 변경
        return accountService.modifyAfterVerify(request);
    }

    /**
     * session 1개 제한
     */
    @GetMapping("/expired")
    public Header expired(){
        return Header.ERROR("세션 만료");
    }
}
