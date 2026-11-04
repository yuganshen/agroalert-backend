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
}
