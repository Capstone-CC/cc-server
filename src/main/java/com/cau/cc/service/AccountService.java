package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MajorRepository;
import com.cau.cc.security.model.CustomUserDetails;
import com.cau.cc.security.token.AjaxAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MajorRepository majorRepository;

    /**
     * 가입 필수 정보 : EMAIL, PW, GENDER, GRADE, MAJOR
     */
    public Header<AccountApiResponse> create(AccountApiRequest request) {

        //verify
        if(!emailCheck(request.getEmail())){
            return Header.ERROR("이미 존재하는 Email 입니다.");
        }


        //TODO : account -> major 단방향
        Major major = majorRepository.findByMajorName(request.getMajorName());


        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .grade(request.getGrade())
                .nickName("푸앙이")
                .build();

        if(major != null){
            account.setMajorId(major);
        }

        Account findAccount2 = accountRepository.save(account);

        //reponser
        AccountApiResponse response = new AccountApiResponse();
        response.setEmail(findAccount2.getEmail());
        response.setGender(findAccount2.getGender());
        response.setGrade(findAccount2.getGrade());
        response.setNickName(findAccount2.getNickName());

        if(findAccount2.getMajorId() != null){
            response.setMajorName(findAccount2.getMajorId().getMajorName());
        }


        return Header.OK(response);
    }

    public boolean emailCheck(String email){
        //validateDuplicateMember
        Account findAccount = accountRepository.findByEmail(email);

        if(findAccount != null){ // 이미존재하는 email
            return false;
        }
        return true;
    }

}