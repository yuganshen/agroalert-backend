// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/LoginRequest.java
package com.agroalert.agroalertbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser una dirección de correo válida")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
