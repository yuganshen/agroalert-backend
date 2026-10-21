// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/UsuarioResponseDto.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Boolean activo;
    private List<String> roles;
}
