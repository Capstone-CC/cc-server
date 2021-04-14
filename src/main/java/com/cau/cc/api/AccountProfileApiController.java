package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.cau.cc.service.AccountProfileService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("") // /api/profile ? email = email@naver.com
    public Header<AccountApiResponse> read(@RequestParam("email") String email ) {
        return accountProfileService.read(email);
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
