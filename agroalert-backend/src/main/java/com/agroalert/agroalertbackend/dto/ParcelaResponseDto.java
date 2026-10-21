// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ParcelaResponseDto.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelaResponseDto {
    private Long id;
    private String nombre;
    private String ubicacion;
    private Double areaHectareas;
    private LocalDate fechaSiembra;
    private Boolean activa;
    private String comunidadNombre;
    private String agricultorNombre;
    private String etapaFenologicaNombre;
}
