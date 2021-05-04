package com.cau.cc.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.repository.AccountRepository;
//import com.cau.cc.model.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

//    @Autowired
//    MajorRepository majorRepository;

    /**
     * 가입 필수 정보 : EMAIL, PW, GENDER, GRADE, MAJOR
     */
    public Header<AccountApiResponse> create(AccountApiRequest request) {

        //verify
        if(!emailCheck(request.getEmail())){
            return Header.ERROR("이미 존재하는 Email 입니다.");
        }


        //TODO : account -> major 단방향
        //Major major = majorRepository.findByMajorName(request.getMajorId().getMajorName());


        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .grade(request.getGrade())
                .nickName("푸앙이")
                .majorName(request.getMajor())
                .build();


        Account findAccount2 = accountRepository.save(account);

        //reponser
        AccountApiResponse response = new AccountApiResponse();
        response.setEmail(findAccount2.getEmail());
        response.setGender(findAccount2.getGender());
        response.setGrade(findAccount2.getGrade());
        response.setMajorName(findAccount2.getMajorName());
        response.setNickName(findAccount2.getNickName());



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