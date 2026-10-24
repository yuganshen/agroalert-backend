package com.agroalert.agroalertbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgroalertBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroalertBackendApplication.class, args);
    }

}
