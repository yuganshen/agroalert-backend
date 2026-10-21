// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ComunidadDto.java
package com.agroalert.agroalertbackend.dto;

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
public class ComunidadDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El distrito no puede estar vacío")
    private String distrito;

    @NotBlank(message = "La provincia no puede estar vacía")
    private String provincia;

    @NotNull(message = "La latitud es requerida")
    private Double latitud;

    @NotNull(message = "La longitud es requerida")
    private Double longitud;
}
