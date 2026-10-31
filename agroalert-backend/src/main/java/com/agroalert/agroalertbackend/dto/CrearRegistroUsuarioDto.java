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
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Builder.Default
    private RolNombre rol = RolNombre.AGRICULTOR;

    private String dni;

    private String telefono;

    private String codigoProfesional;

    private String especialidad;
}
