package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api")
public class AccountApiController implements CrudInterface<AccountApiRequest, AccountApiResponse> {


    @Autowired
    private AccountService accountService;

    @Override
    public Header<AccountApiResponse> create(Header<AccountApiRequest> request) {
        return null;
    }

    @Override
    public Header<AccountApiResponse> read(Long id) {
        return accountService.read(id);
    }

    @Override
    public Header<AccountApiResponse> update(Header<AccountApiRequest> request) {
        return accountService.update(request);
    }

    @Override
    public Header delete(Long id) {
        return accountService.delete(id);
    }
}
