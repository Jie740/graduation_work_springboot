package com.clj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.clj.mapper")
public class AgriculturalSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriculturalSystemApplication.class,args);
    }
}
