package com.mmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mmall.dao")
public class m {

    public static void main(String[] args) {
        SpringApplication.run(m.class,args);
    }
}
