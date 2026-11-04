// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/CrearRegistroUsuarioDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearRegistroUsuarioDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @jakarta.validation.constraints.Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Builder.Default
    private RolNombre rol = RolNombre.AGRICULTOR;

    private String dni;

    private String telefono;

    private String codigoProfesional;

    private String especialidad;

    @jakarta.validation.constraints.AssertTrue(message = "El agricultor debe tener un DNI de 8 dígitos")
    public boolean isDniValido() {
        return rol != RolNombre.AGRICULTOR || (dni != null && dni.matches("[0-9]{8}"));
    }

    @jakarta.validation.constraints.AssertTrue(message = "El agricultor debe tener un teléfono de entre 7 y 12 dígitos")
    public boolean isTelefonoValido() {
        return rol != RolNombre.AGRICULTOR || (telefono != null && telefono.matches("[0-9]{7,12}"));
    }

    @jakarta.validation.constraints.AssertTrue(message = "La contraseña no debe superar 72 bytes UTF-8")
    public boolean isPasswordCompatible() {
        return password == null || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }
}
