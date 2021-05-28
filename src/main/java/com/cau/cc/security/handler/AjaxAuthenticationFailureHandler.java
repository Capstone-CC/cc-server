package com.cau.cc.security.handler;

import com.cau.cc.model.network.Header;
import com.cau.cc.security.exception.UserSuspensionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

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

//        LoginApiResponse loginApiResponse = LoginApiResponse.builder()
//                .result(false)
//                .build();
        response.setStatus(HttpStatus.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (exception instanceof UsernameNotFoundException) {
            objectMapper.writeValue(response.getWriter(), Header.ERROR("존재하지 않는 이메일입니다."));
        } else if (exception instanceof BadCredentialsException) {
            objectMapper.writeValue(response.getWriter(), Header.ERROR("비밀번호가 일치하지 않습니다."));
        } else if (exception instanceof UserSuspensionException) {
            objectMapper.writeValue(response.getWriter(), Header.ERROR("정지된 계정입니다."));
        }
    }
}
