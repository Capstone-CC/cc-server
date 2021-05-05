package com.cau.cc.security.handler;

import com.cau.cc.model.network.Header;
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
        //Account account = (Account) authentication.getPrincipal();

//        LoginApiResponse loginApiResponse = LoginApiResponse.builder()
//                .result(true)
//                .build();


        //response
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //Create Cookie after Create



        objectMapper.writeValue(response.getWriter(), Header.OK());

    }
}
