package com.cau.cc.api;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountService;
import com.cau.cc.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class RegisterApiController {

    @Autowired
    private EmailService emailService;

    /**
     * 이메일받아서 인증보내기
     */
    @GetMapping("/email")
    public Header<String> email(@RequestParam String email,
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

            //response
            response = "email send finished";

            //Header는 static 클래스
            return Header.OK(response);

        } else { // 이메일 형식 아니면
            //response
            response = "Not email format";
            return Header.ERROR(response);

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
    public Header<String> verify(@RequestParam String email,
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

        String response = null;

        if(newBody == null){ //쿠키가 없는경우
            response = "이메일 인증을 해주세요";
            return Header.ERROR(response);
        }

        //파라미터로 받은 newAccount와 기존에있던 originAcountDto code가 같으면
        if(newBody.getVerificationCode().contains(originBody.getVerificationCode())){
            //인증 완료 했으므로 세션에서 지우기
            // TODO : 세선 유지 후 인증 된 사용자임을 저장
            originBody.setCheckEmaile(true);
            // 기존과 동일한 session name으로 들어오면 덮어씌어진다.
            httpSession.setAttribute(originBody.getEmail(),originBody);

            response = "인증 완료";
            return Header.OK(response);

        } else{ // 다르면
            response = "인증번호가 틀렸습니다.";
            return Header.ERROR(response);
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
    public Header<AccountApiResponse> create(@RequestBody Header<AccountApiRequest> request,
                                             HttpSession httpSession) {
        
        //입력받은 객체에 대한 값을 세션에서 꺼내서
        AccountApiRequest origiBody = (AccountApiRequest) httpSession.getAttribute(request.getData().getEmail());
        
        //세션에서 꺼낸 originBody가 인증된 사용자인지 검토
        if(origiBody.isCheckEmaile()){
            // 세션만료
            httpSession.removeAttribute(origiBody.getEmail());

            return accountService.create(request);

        } else{
            return Header.ERROR("세션만료");
        }
    }
}
