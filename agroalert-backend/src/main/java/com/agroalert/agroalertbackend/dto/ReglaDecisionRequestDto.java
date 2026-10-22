// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ReglaDecisionRequestDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
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
public class ReglaDecisionRequestDto {

    @NotBlank(message = "La variable meteorológica es obligatoria")
    private String variableMeteorologica;

    @NotBlank(message = "El operador es obligatorio")
    private String operador;

    @NotNull(message = "El umbral es obligatorio")
    private Double umbral;

    private String fuenteTecnica;

    @NotNull(message = "El nivel resultante es obligatorio")
    private NivelRiesgo nivelResultante;

    @NotNull(message = "El ID del riesgo es obligatorio")
    private Long riesgoId;

    @NotNull(message = "El ID de la etapa fenológica es obligatorio")
    private Long etapaFenologicaId;
}
