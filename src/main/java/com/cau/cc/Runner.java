package com.cau.cc;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.GenderEnum;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.repository.AccountRepository;
import com.cau.cc.model.repository.MajorRepository;
import com.cau.cc.service.AccountService;
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

            Account account = new Account();
            account.setId(1L);
            account.setName("1");
            account.setNickName("1");
            account.setPassword("1");
            account.setImage("1");
            account.setGender(GenderEnum.남);
            account.setAge(1);
            account.setGender(GenderEnum.여);
            account.setHeight(170);
            account.setLocation("서울");
            account.setEmail("1");
            Account account1 = new Account();
            account.setId(2L);
            account.setName("2");
            account.setNickName("1");
            account.setPassword("1");
            account.setImage("1");
            account.setGender(GenderEnum.남);
            account.setAge(1);
            account.setGender(GenderEnum.여);
            account.setHeight(170);
            account.setLocation("서울");
            account.setEmail("1");
            accountRepository.save(account);

            accountRepository.save(account1);

            Major major = majorRepository.findByMajorName(MajorEnum.경영경제대학);

            //test db
            Account account2 = Account.builder()
                    .id(2l)
                    .email("test@gmail.com")
                    .password(passwordEncoder.encode("123123"))
                    .gender(GenderEnum.남)
                    .majorId(major)
                    .grade(1)
                    .build();
            accountRepository.save(account2);



            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());
        }
    }
}
