package com.agroalert.agroalertbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:123456}"
})
class AgroalertBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
