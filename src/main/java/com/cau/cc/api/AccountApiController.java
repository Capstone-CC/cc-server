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
@RequestMapping("/api/account")
public class AccountApiController implements CrudInterface<AccountApiRequest, AccountApiResponse> {


    @Autowired
    private AccountService accountService;



    @Override // 호출안됨
    public Header<AccountApiResponse> create(@RequestBody Header<AccountApiRequest> request) {
        return null;
    }

    @Override
    @GetMapping("")
    public Header<AccountApiResponse> read(@RequestBody Long id) {
        return accountService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<AccountApiResponse> update(@RequestBody Header<AccountApiRequest> request) {
        return accountService.update(request);
    }

    @Override
    @DeleteMapping("")
    public Header delete(@RequestBody Long id) {
        return accountService.delete(id);
    }
}
