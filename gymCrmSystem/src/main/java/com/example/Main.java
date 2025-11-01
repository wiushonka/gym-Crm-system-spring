package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


// I enabled jpa repos just for rest-data endpoints to work, because it turned out they only work for jpa repos
// and also it does not need @EnableEurekaClient since presence of eureka client dependency in pom and .properties is enough
@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.example.storage.repos")
@EnableFeignClients
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}