package com.cau.cc.api;

import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountService;
import com.cau.cc.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api")
public class EmailApiController {

    @Autowired
    private EmailService emailService;

    /**
     * 이메일받아서 인증보내기
     */
    @GetMapping("/email")
    public ResponseEntity<String> email(@RequestBody Header<AccountApiRequest> request,
                                        HttpSession httpSession)
            throws UnsupportedEncodingException, MessagingException {


        AccountApiRequest body = request.getData();

        //email 받아서
        String email = body.getEmail();

        //메일보내고 인증코드 받아서
        String randomCode = emailService.sendVerificationEmail(email);

        //인증코드는 받은 AccountDto에 저장하고
        body.setVerificationCode(randomCode);

        //세션에 받은 이메일을 key로 AccountDTO 객체 Session의 저장
        // TODO : 세션 만료 시간 설정
        httpSession.setAttribute(body.getEmail(),body);

        return ResponseEntity.ok("email send finished");
    }

    /**
     * 코드,이메일을 받아서 인증 하고 맞으면 TRUE 반환, 틀리면 FALSE 반환
     *
     * 이메일을 받지 않은 사용자가 verify 신청한경우는 발생하지 않아야 한다.
     * 클라이언트에선 이메일로 인증코드 받은 경우만 verify 신청 할 수 있도록 해야한다.
     *
     */
    @GetMapping("/verify")
    public boolean verify(@RequestBody Header<AccountApiRequest> request,
                          HttpSession httpSession){

        AccountApiRequest newBody = request.getData();

        //쿠키의 맞는 세션을 받아 해당 세션에서 파라미터로 받은 이메일의 해당하는 ACCOUNT객체 꺼내고
        //해당 객체의 코드와 파라미터로 받은 accountDto의 code를 비교
        AccountApiRequest originBody = (AccountApiRequest) httpSession.getAttribute(newBody.getEmail());

        if(newBody == null){ //쿠키가 없는경우
            return false;
        }

        //세션에서 꺼내온 newAccountDTO와 기존에있던 originAcountDto code가 같으면
        if(newBody.getVerificationCode().contains(originBody.getVerificationCode())){
            //인증 완료 했으므로 세션에서 지우기
            // TODO : 세선 유지 후 인증 된 사용자임을 저장
            originBody.setCheckEmaile(true);
            httpSession.setAttribute(originBody.getName(),originBody);
            return true;
        } else{ // 다르면
            return false;
        }
    }

    @Autowired
    AccountService accountService;

    /**
     * 이메일 인증된 사용자 가입
     * 세션에서 꺼내서  확인 후 나머지 정보 추가 해서 저장
     */
    @PostMapping("/register")
    public String create(@RequestBody Header<AccountApiRequest> request,
                                             HttpSession httpSession) {
        
        //입력받은 객체에 대한 값을 세션에서 꺼내서
        AccountApiRequest origiBody = (AccountApiRequest) httpSession.getAttribute(request.getData().getName());
        
        //세션에서 꺼낸 originBody가 인증된 사용자인지 검토
        if(origiBody.isCheckEmaile()){
            return "가입완료";
        } else{
            return "가입안됨";
        }
    }
}
