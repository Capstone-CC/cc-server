package com.cau.cc.security.provider;

import com.cau.cc.security.model.CustomUserDetails;
import com.cau.cc.security.service.CustomerUserDetailsService;
import com.cau.cc.security.token.AjaxAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

/**
 * 동작 방식은 Form 인증 방식과 동일
 */
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    //AuthenticationManager로 부터 인증요청한 객체 들어온다
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName(); // 인증전 사용자가 입력한 id
        String password = (String)authentication.getCredentials(); // 인증전 사용자가 입력한 pw

        //customerUserDetailsService 부터 객체 얻어오는데 이는
        //인증된 UserDetails 타입의 CustomUserDetails 객체
        CustomUserDetails accountContext = (CustomUserDetails) customerUserDetailsService.loadUserByUsername(username);

        //패스워드 검증
        if(!passwordEncoder.matches(password,accountContext.getPassword())){
            throw  new BadCredentialsException("BadCredentialsException");
        }

        //pw 검증 완료된 인증된 account 객체, pw는 null처리,
        AjaxAuthenticationToken authenticationToken =
                new AjaxAuthenticationToken(accountContext.getAccount(), null,accountContext.getAuthorities());

        return authenticationToken;
    }

    /**
     * AjaxAuthenticationToken과 동일한 타입의 토큰이 들어오면 해당 provider 실행된다.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return AjaxAuthenticationToken.class.isAssignableFrom(aClass);
    }
}