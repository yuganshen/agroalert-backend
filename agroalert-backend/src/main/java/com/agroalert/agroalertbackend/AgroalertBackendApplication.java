package com.agroalert.agroalertbackend;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableAdminServer
public class AgroalertBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroalertBackendApplication.class, args);
    }

}
