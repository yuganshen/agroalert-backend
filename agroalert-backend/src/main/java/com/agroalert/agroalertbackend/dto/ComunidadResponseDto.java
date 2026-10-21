// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ComunidadResponseDto.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComunidadResponseDto {
    private Long id;
    private String nombre;
    private String distrito;
    private String provincia;
    private Double latitud;
    private Double longitud;
    private Boolean activa;
}
