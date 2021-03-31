package com.cau.cc;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class Runner implements ApplicationRunner {
    @Autowired
    DataSource dataSource;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try(Connection connection = dataSource.getConnection()){

            Account account = new Account();
            account.setId(1L);
            account.setName("1");
            account.setNickName("1");
            account.setPassword("1");
            account.setImage("1");
            account.setGender("1");
            account.setAge(1);
            account.setGender("1");
            account.setHeight(170);
            account.setLocation("서울");
            account.setEmail("1");
            Account account1 = new Account();
            account.setId(2L);
            account.setName("2");
            account.setNickName("1");
            account.setPassword("1");
            account.setImage("1");
            account.setGender("1");
            account.setAge(1);
            account.setGender("1");
            account.setHeight(170);
            account.setLocation("서울");
            account.setEmail("1");
            accountRepository.save(account);

            accountRepository.save(account1);
            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());
        }
    }
}
