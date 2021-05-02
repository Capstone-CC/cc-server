package com.cau.cc.security.service;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    /**
     * 로그인시 실행되는 메소드
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("account not found");
        }

        // 해당프로젝트에서 roles은 설정 안했으므로 null
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(account,roles);
    }
}
