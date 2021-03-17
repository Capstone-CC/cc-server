package com.cau.cc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class CcApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcApplication.class, args);


    }

}
