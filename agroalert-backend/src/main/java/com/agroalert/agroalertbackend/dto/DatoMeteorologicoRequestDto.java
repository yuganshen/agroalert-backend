// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/DatoMeteorologicoRequestDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatoMeteorologicoRequestDto {

    @NotNull(message = "El ID de la parcela es obligatorio")
    private Long parcelaId;

    private LocalDateTime fechaHora;

    @NotNull(message = "La temperatura es obligatoria")
    private Double temperatura;

    @NotNull(message = "La precipitación es obligatoria")
    private Double precipitacion;

    @NotNull(message = "La humedad relativa es obligatoria")
    private Double humedadRelativa;

    @NotNull(message = "La velocidad del viento es obligatoria")
    private Double velocidadViento;

    private TipoDato tipoDato;

    private String fuente;
}
