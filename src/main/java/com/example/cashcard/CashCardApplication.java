package com.example.cashcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CashCardApplication {
    public static void main(String[] args) {
        CashCardApplication app = new CashCardApplication();

        SpringApplication.run(app.getClass(), args);
    }
}
