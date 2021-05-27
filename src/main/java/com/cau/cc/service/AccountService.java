package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountFindRequest;
import com.cau.cc.model.network.request.AccountModifyRequest;
import com.cau.cc.model.network.request.RegisterApiRequest;
import com.cau.cc.model.repository.AccountRepository;
//import com.cau.cc.model.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    /**
     * 가입 필수 정보 : EMAIL, PW, GENDER, GRADE, MAJOR
     */
    public Account create(RegisterApiRequest request) {

        //veri
        if(!emailCheck(request.getEmail())){
            return null;
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .grade(request.getGrade())
                .nickName("푸앙이")
                .majorName(request.getMajorName())
                .reportedCount(0)
                .reporterCount(3)
                .build();


        return accountRepository.save(account);
    }

    /**
     * 이메일이 존재하면 false
     * 이메일이 존재하지 않으면 true
     * @param email
     * @return
     */
    public boolean emailCheck(String email){
        Account findAccount = accountRepository.findByEmail(email);

        if(findAccount != null){ // 이미존재하는 email
            return false;
        }
        return true;
    }

    /**
     * 로그인 사용자 비밀번호 변경
     */
    public Header modify(AccountModifyRequest request){

        Account origin = accountRepository.findByEmail(request.getEmail());

        /**입력한 비번이 맞는지 (암호화 되지않은 password와 암호화된 password 비교)**/
        if(!passwordEncoder.matches(request.getOriginPw(), origin.getPassword())){
            return Header.ERROR("현재 비밀번호가 틀렸습니다.");
        }
        
        /** 비번은 동일하므로 변경할 비번 동일 한지 확인 **/
        if(!request.getPassword().equals(request.getConfirmPw())){
            return Header.ERROR("변경 할 비밀번호가 서로 틀립니다.");
        }
        
        /**변경 할 비번 동일**/
        String newPassword = passwordEncoder.encode(request.getPassword());
        origin.setPassword(newPassword);
        accountRepository.save(origin);
        return Header.OK();
    }

    /**
     * 비 로그인 사용자 비밀번호 변경
     */
    public Header modifyAfterVerify(AccountFindRequest request){

        Account origin = accountRepository.findByEmail(request.getEmail());

        /** 변경할 비번 동일 한지 확인 **/
        if(!request.getChangePw().equals(request.getConfirmPw())){
            return Header.ERROR("변경 할 비밀번호가 서로 틀립니다.");
        }

        /**변경 할 비번 동일**/
        String newPassword = passwordEncoder.encode(request.getChangePw());
        origin.setPassword(newPassword);
        accountRepository.save(origin);
        return Header.OK();
    }



}