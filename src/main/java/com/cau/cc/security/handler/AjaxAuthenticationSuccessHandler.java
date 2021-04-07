package com.cau.cc.security.handler;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.network.response.AccountApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증의 최종 성공한 Entity인 Account 객체가 들어있다. getPrincipal() 통해 빼오기기
        Account account = (Account) authentication.getPrincipal();

        AccountApiResponse accountApiResponse = AccountApiResponse.builder()
                .email(account.getEmail())
                .gender(account.getGender())
                .grade(account.getGrade())
                //TODO : major
                .build();

        //response
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), accountApiResponse);

    }
}
