// Ruta: src/test/java/com/agroalert/agroalertbackend/security/JwtAuthFilterTest.java
package com.agroalert.agroalertbackend.security;

import com.agroalert.agroalertbackend.repository.TokenJwtRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private TokenJwtRepository tokenJwtRepository;

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService, tokenJwtRepository);
    }

    @Test
    void shouldNotFilter_RutasSpringBootAdminYRecursosEstaticos_RetornaTrue() throws ServletException {
        String[] rutasExcluidas = {
                "/applications",
                "/applications/12345",
                "/instances",
                "/instances/abcdef",
                "/assets/css/style.css",
                "/variables.css",
                "/sba-settings.js",
                "/actuator",
                "/actuator/health",
                "/actuator/info",
                "/wallboard",
                "/journal",
                "/about",
                "/notifications",
                "/notifications/summary",
                "/",
                "/index.html",
                "/favicon.ico"
        };

        for (String ruta : rutasExcluidas) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath(ruta);
            assertTrue(jwtAuthFilter.shouldNotFilter(request), "La ruta " + ruta + " debería ser excluida del filtrado JWT.");
        }
    }

    @Test
    void shouldNotFilter_RutasProtegidas_RetornaFalse() throws ServletException {
        String[] rutasProtegidas = {
                "/api/parcelas",
                "/api/alertas",
                "/api/usuarios",
                "/api/riesgos",
                "/api/medidas"
        };

        for (String ruta : rutasProtegidas) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath(ruta);
            assertFalse(jwtAuthFilter.shouldNotFilter(request), "La ruta " + ruta + " NO debería ser excluida del filtrado JWT.");
        }
    }
}
