package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT u FROM Account u WHERE u.email = ?1")
    Account findByEmail(String email);

    @Query("SELECT u FROM Account u  WHERE u.verificationCode = ?1")
    public Account findByVerificationCode(String code);

    public Account findByname(String name);
}
