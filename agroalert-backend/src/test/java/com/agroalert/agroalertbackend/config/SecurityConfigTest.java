// Ruta: src/test/java/com/agroalert/agroalertbackend/config/SecurityConfigTest.java
package com.agroalert.agroalertbackend.config;

import com.agroalert.agroalertbackend.security.JwtAuthFilter;
import com.agroalert.agroalertbackend.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);
    }

    @Test
    void testCorsConfigurationSource_ConfiguracionPorDefecto() {
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", List.of("http://localhost:3000", "http://localhost:5173"));

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/alertas");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertEquals(List.of("http://localhost:3000", "http://localhost:5173"), config.getAllowedOrigins());
        assertTrue(config.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
        assertTrue(config.getAllowedHeaders().containsAll(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With")));
        assertTrue(Boolean.TRUE.equals(config.getAllowCredentials()));
    }

    @Test
    void testCorsConfigurationSource_ConListaVacia_UsaValoresPorDefecto() {
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", List.of());

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/parcelas");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertEquals(List.of("http://localhost:3000", "http://localhost:5173"), config.getAllowedOrigins());
        assertTrue(Boolean.TRUE.equals(config.getAllowCredentials()));
    }

    @Test
    void testCorsConfigurationSource_OrigenesPersonalizados() {
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", List.of("https://agroalert.pe", "https://admin.agroalert.pe"));

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertEquals(List.of("https://agroalert.pe", "https://admin.agroalert.pe"), config.getAllowedOrigins());
    }
}
