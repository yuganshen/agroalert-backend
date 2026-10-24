// Ruta: src/main/java/com/agroalert/agroalertbackend/config/AppConfig.java
package com.agroalert.agroalertbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
