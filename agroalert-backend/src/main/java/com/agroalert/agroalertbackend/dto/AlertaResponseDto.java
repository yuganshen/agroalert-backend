// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/AlertaResponseDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaResponseDto {
    private Long id;
    private LocalDateTime generadaEn;
    private String condicionDetectada;
    private NivelRiesgo nivel;
    private EstadoAlerta estado;
    private Long parcelaId;
    private String parcelaNombre;
    private String riesgoNombre;
    private String medidaNombre;
    private String recomendacionTexto;
    private String procedimientoTitulo;
}
