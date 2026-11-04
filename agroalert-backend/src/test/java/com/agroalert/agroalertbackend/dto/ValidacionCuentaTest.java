// Ruta: src/test/java/com/agroalert/agroalertbackend/dto/ValidacionCuentaTest.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidacionCuentaTest {
    private CrearRegistroUsuarioDto agricultor() {
        return CrearRegistroUsuarioDto.builder().nombre("Dato de ejemplo").apellido("Prueba")
                .email("ejemplo@example.test").password("Prueba123!").rol(RolNombre.AGRICULTOR)
                .dni("00000001").telefono("000000000").build();
    }
    private boolean valido(Object dto) {
        try (var factory=Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().validate(dto).isEmpty();
        }
    }
    @Test void aceptaAgricultorCompleto() { assertTrue(valido(agricultor())); }
    @Test void rechazaPasswordCorto() { var d=agricultor(); d.setPassword("12345"); assertFalse(valido(d)); }
    @Test void rechazaPasswordVacio() { var d=agricultor(); d.setPassword("      "); assertFalse(valido(d)); }
    @Test void rechazaPasswordQueExcedeBCryptEnBytes() { var d=agricultor(); d.setPassword("á".repeat(37)); assertFalse(valido(d)); }
    @Test void rechazaDniAusenteYNoNumerico() { var d=agricultor(); d.setDni(null); assertFalse(valido(d)); d.setDni("ABCDEFGH"); assertFalse(valido(d)); }
    @Test void rechazaTelefonoInvalido() { var d=agricultor(); d.setTelefono("123"); assertFalse(valido(d)); d.setTelefono(null); assertFalse(valido(d)); }
    @Test void otrosRolesNoExigenDniNiTelefono() { var d=agricultor(); d.setRol(RolNombre.TECNICO); d.setDni(null); d.setTelefono(null); assertTrue(valido(d)); }
    @Test void editarOCambiarAAgricultorExigeCampos() {
        var d=new ActualizarUsuarioDto(); d.setNombre("Dato de ejemplo");d.setApellido("Prueba");d.setEmail("ejemplo@example.test");d.setRol(RolNombre.AGRICULTOR);
        assertFalse(valido(d));d.setDni("00000001");d.setTelefono("000000000");assertTrue(valido(d));
    }
}
