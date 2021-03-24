package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class AccountRepositoryTest {

    @Autowired //의존성 주입 (직접 객체만들지 말고 스프링이 관리해줌)
    private AccountRepository accountRepository;

    @Test
    public void create() {

        Long id = 1L;
        String name = "정지광";
        String nickName = "코끼리";
        String password = "Test01@gmail.com";
        String image = "image";
        String gender = "man";
        int age = 1;
        int grade = 4;
        int height = 180;
        String location = "서울";
        String email = "wlrhkd49@naver.com";


        Account user = new Account();
        user.setId(id);
        user.setName(name);
        user.setNickName(nickName);
        user.setPassword(password);
        user.setImage(image);
        user.setGender(gender);
        user.setAge(age);
        user.setGrade(grade);
        user.setHeight(height);
        user.setLocation(location);
        user.setEmail(email);

        Account newUser = accountRepository.save(user);

        Assertions.assertNotNull(newUser);


    }
}

