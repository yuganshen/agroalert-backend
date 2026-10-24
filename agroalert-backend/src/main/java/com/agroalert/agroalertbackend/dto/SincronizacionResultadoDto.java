// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/SincronizacionResultadoDto.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacionResultadoDto {

    private LocalDateTime ejecutadoEn;
    private int totalParcelasProcesadas;
    private int parcelasSincronizadasExitosamente;
    private int parcelasConError;
    private int totalAlertasGeneradas;

    @Builder.Default
    private List<String> detalles = new ArrayList<>();
}
