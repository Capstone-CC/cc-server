package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Chatroom;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.request.ChatroomApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.network.response.ChatroomApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

        Account account = Account.builder()
                .email(body.getEmail())
                .password(passwordEncoder.encode(body.getPassword()))
                .gender(body.getGender())
                .grade(body.getGrade())
                //TODO : major 저장
                //TODO : major의 account 저장
                .build();

        Account findAccount2 = accountRepository.save(account);

        //reponser
        AccountApiResponse response = new AccountApiResponse();
        response.setEmail(findAccount2.getEmail());
        response.setGender(findAccount2.getGender());
        response.setGrade(findAccount2.getGrade());
        //TODO : major 추가

        return Header.OK(response);
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