package com.example.springevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class SpringEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringEventApplication.class, args);
    }

}
