package com.cau.cc.security.authapi;

import com.cau.cc.model.network.request.AccountApiRequest;
import com.cau.cc.model.network.request.LoginApiRequest;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * API용도
 * spring security filter가 가로채서 처리 하므로 실행되지 않는다.
 */
@Api(tags = "로그인 & 로그아웃")
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AuthApi {

    @ApiOperation(value = "로그인",notes = "이메일과 password만 입력")
    @PostMapping("/login")
    public void fakeLogin(@ApiParam(value = "email & password") @RequestBody LoginApiRequest loginApiRequest) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @ApiOperation(value = "로그아웃",notes = "쿠키만 필요")
    @PostMapping("/logout")
    public void fakeLogin() {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }
}

