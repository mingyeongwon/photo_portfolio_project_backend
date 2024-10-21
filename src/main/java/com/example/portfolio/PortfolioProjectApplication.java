package com.example.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PortfolioProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioProjectApplication.class, args);
    }
}
