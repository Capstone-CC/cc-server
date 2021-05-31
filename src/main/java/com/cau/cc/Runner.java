package com.cau.cc;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class Runner implements ApplicationRunner {
    @Autowired
    DataSource dataSource;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        try(Connection connection = dataSource.getConnection()){

//            Account account2 = Account.builder()
//            //        .id(2l)
//
//                    .email("test1")
//                    .password(passwordEncoder.encode("123123"))
//                    .gender(GenderEnum.남)
//                    .majorName(MajorEnum.생명과학과)
//                    .nickName("푸앙이")
//                    .grade(1)
//                    .count(100)
//                    .reporterCount(3)
//                    .reportedCount(0)
//                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
//                    .build();
//
//            accountRepository.save(account2);
//
//            Account account3 = Account.builder()
//                    //        .id(2l)
//                    .email("test2")
//                    .password(passwordEncoder.encode("123123"))
//                    .gender(GenderEnum.여)
//                    .majorName(MajorEnum.수학과)
//                    .grade(1)
//                    .count(100)
//                    .reporterCount(3)
//                    .reportedCount(0)
//                    .nickName("푸앙이")
//                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
//                    .build();
//
//            accountRepository.save(account3);
//
            Account account4 = Account.builder()
                    //        .id(2l)
                    .email("test7")
                    .password(passwordEncoder.encode("123123"))
                    .gender(GenderEnum.남)
                    .majorName(MajorEnum.수학과)
                    .grade(1)
                    .count(100)
                    .reporterCount(3)
                    .reportedCount(0)
                    .nickName("푸앙이")
                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
                    .build();

            accountRepository.save(account4);

//            Account account5 = Account.builder()
//                    //        .id(2l)
//                    .email("test4")
//                    .password(passwordEncoder.encode("123123"))
//                    .gender(GenderEnum.여)
//                    .majorName(MajorEnum.수학과)
//                    .grade(1)
//                    .count(100)
//                    .reporterCount(3)
//                    .reportedCount(0)
//                    .nickName("푸앙이")
//                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
//                    .build();
//
//            accountRepository.save(account5);
//
            Account account6 = Account.builder()
                    //        .id(2l)
                    .email("test9")
                    .password(passwordEncoder.encode("123123"))
                    .gender(GenderEnum.남)
                    .majorName(MajorEnum.수학과)
                    .grade(1)
                    .count(100)
                    .reporterCount(3)
                    .reportedCount(0)
                    .nickName("푸앙이")
                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
                    .build();

            accountRepository.save(account6);

            Account account7 = Account.builder()
                    //        .id(2l)
                    .email("test8")
                    .password(passwordEncoder.encode("123123"))
                    .gender(GenderEnum.여)
                    .majorName(MajorEnum.수학과)
                    .grade(4)
                    .count(100)
                    .reporterCount(3)
                    .reportedCount(0)
                    .nickName("푸앙이")
                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
                    .build();

            accountRepository.save(account7);


            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());
        }
    }
}
