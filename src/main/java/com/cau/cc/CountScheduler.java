package com.cau.cc;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CountScheduler {

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(cron = "0 0 6 * * * ")
    public void run() {
        List<Account> list = accountRepository.findAll();
        list.forEach(
                list1 -> {
                    list1.setCount(100);
                    accountRepository.save(list1);
                });

    }
}

