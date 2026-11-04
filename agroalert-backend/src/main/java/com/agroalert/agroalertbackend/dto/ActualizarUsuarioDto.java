// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ActualizarUsuarioDto.java
package com.agroalert.agroalertbackend.dto;
import jakarta.validation.constraints.*;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import lombok.Data;
@Data
public class ActualizarUsuarioDto {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotBlank @Email private String email;
    @NotNull private RolNombre rol;
    private String dni, telefono, codigoProfesional, especialidad;

    @AssertTrue(message = "El agricultor debe tener un DNI de 8 dígitos")
    public boolean isDniValido() {
        return rol != RolNombre.AGRICULTOR || (dni != null && dni.matches("[0-9]{8}"));
    }

    @AssertTrue(message = "El agricultor debe tener un teléfono de entre 7 y 12 dígitos")
    public boolean isTelefonoValido() {
        return rol != RolNombre.AGRICULTOR || (telefono != null && telefono.matches("[0-9]{7,12}"));
    }
}
