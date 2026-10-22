// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ReglaDecisionResponseDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaDecisionResponseDto {
    private Long id;
    private String variableMeteorologica;
    private String operador;
    private Double umbral;
    private String fuenteTecnica;
    private NivelRiesgo nivelResultante;
    private String riesgoNombre;
    private String etapaFenologicaNombre;
}
