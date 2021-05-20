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

            /**
             * DB init
//             */
//            Major major1 = null;
//            long i=0l;
//            for(MajorEnum m : MajorEnum.values()){
//                major1 = new Major();
//                major1.setMajorName(m);
//                major1.setId(++i);
//            }
//
//            Major major = majorRepository.findByMajorName(MajorEnum.경영경제대학);
//
//
//            test db
//            Account account2 = Account.builder()
//            //        .id(2l)
//
//                    .email("test1")
//                    .password(passwordEncoder.encode("123123"))
//                    .gender(GenderEnum.남)
//                    .majorName(MajorEnum.생명과학과)
//                    .nickName("푸앙이")
//                    .grade(1)
//                    .count(4)
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
//                    .grade(3)
//                    .count(4)
//                    .nickName("푸앙이")
//                    .image("https://caucampuscontact.s3.amazonaws.com/images/logo.png")
//                    .build();
//
//            accountRepository.save(account3);
//            accountRepository.save(account3);           //        .id(2l)


            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());
        }
    }
}
