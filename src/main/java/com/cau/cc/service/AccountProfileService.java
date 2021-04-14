package com.cau.cc.service;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.model.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountProfileService implements CrudInterface<AccountApiRequest, AccountApiResponse> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Header<AccountApiResponse> create(AccountApiRequest request) {
        return null;
    }

    @Override
    public Header<AccountApiResponse> read(Long id) {
        return null;
    }

    @Override
    public Header<AccountApiResponse> update(AccountApiRequest request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

}
