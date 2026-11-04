// Ruta: src/main/java/com/agroalert/agroalertbackend/config/AppConfig.java
package com.agroalert.agroalertbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); factory.setReadTimeout(15000);
        return new RestTemplate(factory);
    }
}
