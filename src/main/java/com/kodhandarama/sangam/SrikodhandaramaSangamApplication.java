package com.kodhandarama.sangam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SrikodhandaramaSangamApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrikodhandaramaSangamApplication.class, args);
    }
}