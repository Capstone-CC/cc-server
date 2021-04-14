package com.cau.cc.security.handler;

import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.response.LoginApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        String errMsg = "Invaild Username or Password";



//        if(exception instanceof BadCredentialsException){
//            errMsg = "Invalid Username or Password";
//        } else if( exception instanceof CredentialsExpiredException){
//            errMsg = "Expired password";
//        }

        LoginApiResponse loginApiResponse = LoginApiResponse.builder()
                .result(false)
                .build();
        response.setStatus(HttpStatus.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), Header.OK(loginApiResponse));
    }
}
