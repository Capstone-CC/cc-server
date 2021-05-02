package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountProfileService {

    @Autowired
    private AccountRepository accountRepository;

    public Header<AccountApiResponse> create(AccountApiRequest request) {
        return null;
    }

    public Header<AccountApiResponse> read(String email) {
        Account account = accountRepository.findByEmail(email);
        if(account!=null) {
            AccountApiResponse accountApiResponse = response(account);
            return Header.OK(accountApiResponse);
        }
        else return Header.ERROR("데이터 없음");

    }
// 이멜, 닉네임, 성별, 학과, 학년, 자기소개
    private AccountApiResponse response(Account account) {
        AccountApiResponse  accountApiResponse = AccountApiResponse.builder()
                .email(account.getEmail())
                .gender(account.getGender())
                .grade(account.getGrade())
                .majorName(account.getMajorId().getMajorName())
                .nickName(account.getNickName())
                .content(account.getContent())
                .build();

        return accountApiResponse;
    }

    @Autowired
    MajorRepository majorRepository;

    public Header<AccountApiResponse> update(AccountApiRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail());

        //major 테이블에서 majorname을 통해서 majorId 값 받아오기
        Major major =  majorRepository.findByMajorName(request.getMajorName());

        if(account != null) {
            account.setNickName(request.getNickName())
                    .setEmail(request.getEmail())
                    .setGender(request.getGender())
                    .setGrade(request.getGrade())
                    .setMajorId(major)
                    .setContent(request.getContent());

            Account updateAccount = accountRepository.save(account);
            AccountApiResponse accountApiResponse = response(updateAccount);
            return Header.OK(accountApiResponse);
        }
        else return Header.ERROR("데이터 없음");


    }

    public Header delete(Long id) {
        return null;
    }

}
