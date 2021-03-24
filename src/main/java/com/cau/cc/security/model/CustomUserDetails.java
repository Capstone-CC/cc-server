package com.cau.cc.security.model;

import com.cau.cc.model.entity.Account;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private Account account;

    public CustomUserDetails(Account myUser,
                             Collection<? extends GrantedAuthority> authorities) {
        super(myUser.getEmail(), myUser.getPassword(), authorities);
        this.account = myUser;
    }

    public String getEmail() {
        return account.getEmail() + " " + account.getEmail();
    }
}
