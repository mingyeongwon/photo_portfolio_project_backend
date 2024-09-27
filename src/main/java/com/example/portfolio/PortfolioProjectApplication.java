package com.example.portfolio;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PortfolioProjectApplication {

    public static void main(String[] args) {
        // .env 파일에서 환경 변수를 로드
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        // Spring Boot가 사용할 수 있도록 시스템 프로퍼티로 설정
        System.setProperty("spring.datasource.url", dbUrl);
        System.setProperty("spring.datasource.username", username);
        System.setProperty("spring.datasource.password", password);

        // 애플리케이션 실행
        SpringApplication.run(PortfolioProjectApplication.class, args);
    }
}
