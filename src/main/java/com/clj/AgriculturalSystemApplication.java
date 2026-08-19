package com.clj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgriculturalSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriculturalSystemApplication.class,args);
    }
}
