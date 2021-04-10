package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.network.response.LoginApiResponse;
import com.cau.cc.security.token.AjaxAuthenticationToken;
import com.cau.cc.service.AccountService;
import com.cau.cc.service.EmailService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RegisterApiController {

    @Autowired
    private EmailService emailService;

    /**
     * 이메일받아서 인증보내기
     */
    @GetMapping("/email")
    public LoginApiResponse email(@RequestParam String email,
            //@RequestBody Header<AccountApiRequest> request,
                                        HttpSession httpSession)
            throws UnsupportedEncodingException, MessagingException {

        AccountApiRequest body = new AccountApiRequest();

        body.setEmail(email);
        //email 받아서
 //       String email = body.getEmail();

        //response
        String response = null;

        if(email.contains("@")){ //이메일 형식이면
            //메일보내고 인증코드 받아서
            String randomCode = emailService.sendVerificationEmail(email);

            //인증코드는 받은 AccountDto에 저장하고
            body.setVerificationCode(randomCode);

            //세션에 받은 이메일을 key로 AccountDTO 객체 Session의 저장
            //세션 만료 시간 3600
            httpSession.setAttribute(body.getEmail(),body);

            //Header는 static 클래스
            LoginApiResponse loginApiResponse = LoginApiResponse.builder()
                    .result(true)
                    .build();
            return loginApiResponse;

        } else { // 이메일 형식 아니면
            LoginApiResponse loginApiResponse1 = LoginApiResponse.builder()
                    .result(false)
                    .build();
            return loginApiResponse1;
//            response = "Not email format";
//            return Header.ERROR(response);

        }
    }

    /**
     * 코드,이메일을 받아서 인증 하고 맞으면 TRUE 반환, 틀리면 FALSE 반환
     *
     * 이메일을 받지 않은 사용자가 verify 신청한경우는 발생하지 않아야 한다.
     * 클라이언트에선 이메일로 인증코드 받은 경우만 verify 신청 할 수 있도록 해야한다.
     *
     */
    @GetMapping("/verify")
    public LoginApiResponse verify(@RequestParam String email,
            @RequestParam String code,
            //@RequestBody Header<AccountApiRequest> request,
                          HttpSession httpSession){

//        AccountApiRequest newBody = request.getData();
        AccountApiRequest newBody = new AccountApiRequest();
        newBody.setEmail(email);
        newBody.setVerificationCode(code);

        //쿠키의 맞는 세션을 받아 해당 세션에서 파라미터로 받은 이메일의 해당하는 ACCOUNT객체 꺼내고
        //해당 객체의 코드와 파라미터로 받은 accountDto의 code를 비교
        AccountApiRequest originBody = (AccountApiRequest) httpSession.getAttribute(newBody.getEmail());

//        String response = null;

        if(newBody == null){ //쿠키가 없는경우
            LoginApiResponse loginApiResponse1 = LoginApiResponse.builder()
                    .result(false)
                    .build();
            return loginApiResponse1;
//            response = "이메일 인증을 해주세요";
//            return Header.ERROR(response);
        }

        //파라미터로 받은 newAccount와 기존에있던 originAcountDto code가 같으면
        if(newBody.getVerificationCode().contains(originBody.getVerificationCode())){
            // TODO : 세선 유지 후 인증 된 사용자임을 저장
            originBody.setCheckEmaile(true);
            // 기존과 동일한 session name으로 들어오면 덮어씌어진다.
            httpSession.setAttribute(originBody.getEmail(),originBody);


            LoginApiResponse loginApiResponse = LoginApiResponse.builder()
                    .result(true)
                    .build();
            return loginApiResponse;

        } else{ // 다르면
            LoginApiResponse loginApiResponse1 = LoginApiResponse.builder()
                    .result(false)
                    .build();
            return loginApiResponse1;
//            response = "인증번호가 틀렸습니다.";
//            return Header.ERROR(response);
        }
    }

    @Autowired
    AccountService accountService;


    /**
     * 이메일 인증된 사용자 가입
     * 세션에서 꺼내서  확인 후 나머지 정보 추가 해서 저장
     *
     * 가입 필수 정보 : EMAIL, PW, GENDER, GRADE, MAJOR
     */
    @PostMapping("/register")
    public LoginApiResponse create(@RequestBody Header<AccountApiRequest> request,
                                           HttpSession httpSession) {



        //입력받은 객체에 대한 값을 세션에서 꺼내서
        AccountApiRequest origiBody = (AccountApiRequest) httpSession.getAttribute(request.getData().getEmail());
        
        //세션에서 꺼낸 originBody가 인증된 사용자인지 검토
        if(origiBody.isCheckEmaile()){

            LoginApiResponse loginApiResponse1 = null;

            System.out.println(request.getData().getGender());
            System.out.println(request.getData().getMajorName());

            // 2개의 비번 틀리면 return
            if(!request.getData().getPassword().equals(request.getData().getConfirmPw())){
                loginApiResponse1 = LoginApiResponse.builder()
                        .result(false)
                        .build();
                return loginApiResponse1;
                //return Header.ERROR("비밀번호 확인 오류");
            }
            if(request.getData().getEmail() == null){
                loginApiResponse1 = LoginApiResponse.builder()
                        .result(false)
                        .build();
                return loginApiResponse1;
                //return Header.ERROR("이메일 정보를 입력해주세요");
            }

            if(!isGender(request.getData().getGender())){
                loginApiResponse1 = LoginApiResponse.builder()
                        .result(false)
                        .build();
                return loginApiResponse1;
                //return Header.ERROR("성별정보 오류");
            }

            //학과정보 올바른지 확인
            if(!isMajor(request.getData().getMajorName())){
                loginApiResponse1 = LoginApiResponse.builder()
                        .result(false)
                        .build();
                return loginApiResponse1;
                //return Header.ERROR("학과정보 오류");
            }


            // 가입완료
            accountService.create(request);

            // 세션만료
            httpSession.removeAttribute(origiBody.getEmail());


            /**
             * 강제로그인
             */
            //권한 가져와서
            List<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));

            //파라미터로 받은 id, pw 토큰 생성
            Authentication ajaxAuthenticationToken =
                    new AjaxAuthenticationToken(request.getData().getEmail(), request.getData().getPassword(),roles);

            //인증 성공한것으로 Context의 Authentication 객체 저장
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(ajaxAuthenticationToken);

            httpSession.setAttribute("SPRING_SECURITY_CONTEXT",securityContext);   // 세션에 spring security context 넣음

            LoginApiResponse loginApiResponse = LoginApiResponse.builder()
                    .result(true)
                    .build();
            return loginApiResponse;

        } else{
            LoginApiResponse loginApiResponse1 = LoginApiResponse.builder()
                    .result(false)
                    .build();
            return loginApiResponse1;
        }
    }

    private boolean isGender(GenderEnum gender) {
        int check = 0;
        for(GenderEnum g : GenderEnum.values()){
            if(g == gender){
                check = 1;
            }
        }
        if (check==1){
            return true;
        }
        return false;
    }

    private boolean isMajor(MajorEnum majorName) {
        int check = 0;
        for(MajorEnum m : MajorEnum.values()){
            if(m == majorName){
                check = 1;
            }
        }
        if (check==1){
            return true;
        }
        return false;
    }


    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
