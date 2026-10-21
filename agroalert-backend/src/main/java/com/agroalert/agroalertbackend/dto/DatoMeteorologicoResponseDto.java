// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/DatoMeteorologicoResponseDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatoMeteorologicoResponseDto {
    private Long id;
    private Long parcelaId;
    private String parcelaNombre;
    private LocalDateTime fechaHora;
    private Double temperatura;
    private Double precipitacion;
    private Double humedadRelativa;
    private Double velocidadViento;
    private TipoDato tipoDato;
    private String fuente;
}
