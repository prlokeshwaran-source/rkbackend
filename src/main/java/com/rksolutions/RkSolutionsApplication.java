package com.rksolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rksolutions")
public class RkSolutionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RkSolutionsApplication.class, args);
    }
}
