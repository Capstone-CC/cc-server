package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.entity.Major;
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
public class AccountService implements  CrudInterface<AccountApiRequest, AccountApiResponse> {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MajorRepository majorRepository;

    /**
     * 가입 필수 정보 : EMAIL, PW, GENDER, GRADE, MAJOR
     */
    @Override
    public Header<AccountApiResponse> create(Header<AccountApiRequest> request) {
        AccountApiRequest body = request.getData();

        //validateDuplicateMember
        Account findAccount = accountRepository.findByEmail(body.getEmail());

        if(findAccount != null){ // 이미존재하는 email
           return Header.ERROR("이미 존재하는 Email 입니다.");
        }

        //TODO : account -> major 단방향
        Major major = majorRepository.findByMajorName(body.getMajorName());

        Account account = Account.builder()
                .email(body.getEmail())
                .password(passwordEncoder.encode(body.getPassword()))
                .gender(body.getGender())
                .grade(body.getGrade())
                .majorId(major)
                .build();


        Account findAccount2 = accountRepository.save(account);

        //reponser
        AccountApiResponse response = new AccountApiResponse();
        response.setEmail(findAccount2.getEmail());
        response.setGender(findAccount2.getGender());
        response.setGrade(findAccount2.getGrade());
        response.setMajorName(findAccount2.getMajorId().getMajorName());
        //TODO : major 추가

        return Header.OK(response);
    }


    public void login(String id, String pw){

    }

    @Override
    public Header<AccountApiResponse> read(Long id) {
        return null;
    }

    @Override
    public Header<AccountApiResponse> update(Header<AccountApiRequest> request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

}