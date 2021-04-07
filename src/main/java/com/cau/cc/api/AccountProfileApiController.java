package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class AccountProfileApiController implements CrudInterface<AccountApiRequest, AccountApiResponse> {

    @Autowired
    private AccountProfileService accountProfileService;

    @Override
    @PostMapping("") // /api/profile
    public Header<AccountApiResponse> create(@RequestBody Header<AccountApiRequest> request) {
        return accountProfileService.create(request);
    }

    @Override
    @GetMapping("{id}") // /api/profile/id
    public Header<AccountApiResponse> read(@PathVariable Long id) {
        return accountProfileService.read(id);
    }

    @Override
    @PutMapping("") // /api/profile
    public Header<AccountApiResponse> update(@RequestBody Header<AccountApiRequest> request) {
        return accountProfileService.update(request);
    }

    @Override
    @DeleteMapping("{id}") // /api/profile/id
    public Header delete(@PathVariable Long id) {
        return accountProfileService.delete(id);
    }
}
