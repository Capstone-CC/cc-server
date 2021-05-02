package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class AccountProfileApiController {

    @Autowired
    private AccountProfileService accountProfileService;

    @PostMapping("") // /api/profile
    public Header<AccountApiResponse> create(@RequestBody AccountApiRequest request) {
        return accountProfileService.create(request);
    }

    @GetMapping("")
    public Header<AccountApiResponse> read() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) auth.getPrincipal();

        if(account.getEmail() == null) {
            Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
            String email2 = auth2.getName();
            return accountProfileService.read(email2);
        }
        return accountProfileService.read(account.getEmail());
    }

    @PutMapping("") // /api/profile
    public Header<AccountApiResponse> update(@RequestBody AccountApiRequest request) {
        return accountProfileService.update(request);
    }

    @DeleteMapping("{id}") // /api/profile/id
    public Header delete(@PathVariable Long id) {
        return accountProfileService.delete(id);
    }
}
