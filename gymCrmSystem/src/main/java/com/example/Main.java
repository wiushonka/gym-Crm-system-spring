package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;


// I enabled jpa repos just for rest-data endpoints to work, because it turned out they only work for jpa repos
@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.example.storage.repos")
@EnableJms
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}