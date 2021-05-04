package com.cau.cc;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MajorRepository;
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

    @Autowired
    MajorRepository majorRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try(Connection connection = dataSource.getConnection()){

            /**
             * DB init
             */
            Major major1 = null;
            long i=0l;
            for(MajorEnum m : MajorEnum.values()){
                major1 = new Major();
                major1.setMajorName(m);
                major1.setId(++i);
                majorRepository.save(major1);
            }

            Major major = majorRepository.findByMajorName(MajorEnum.경영경제대학);


            //test db
            Account account2 = Account.builder()
                    .id(2l)
                    .email("test")
                    .password(passwordEncoder.encode("123123"))
                    .gender(GenderEnum.남)
                    .majorName(MajorEnum.경영경제대학)
                    .grade(1)
                    .build();
            accountRepository.save(account2);



            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());
        }
    }
}
