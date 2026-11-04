// Ruta: src/test/java/com/agroalert/agroalertbackend/security/JwtServiceTest.java
package com.agroalert.agroalertbackend.security;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test void cadaLoginTieneIdentificadorUnicoAunqueLosClaimsCoincidan() {
        var service=new JwtService();
        ReflectionTestUtils.setField(service,"secretKey","0123456789012345678901234567890123456789012345678901234567890123");
        ReflectionTestUtils.setField(service,"jwtExpiration",86400000L);
        var a=service.generarToken(Map.of(),"dato@example.test");
        var b=service.generarToken(Map.of(),"dato@example.test");
        assertNotNull(service.extractAllClaims(a).getId());
        assertNotEquals(service.extractAllClaims(a).getId(),service.extractAllClaims(b).getId());
        assertNotEquals(a,b);assertTrue(service.isTokenValid(a));
    }
}
