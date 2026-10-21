// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/DatoMeteorologicoController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoResponseDto;
import com.agroalert.agroalertbackend.service.DatoMeteorologicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datos-meteorologicos")
@RequiredArgsConstructor
public class DatoMeteorologicoController {

    private final DatoMeteorologicoService datoMeteorologicoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<DatoMeteorologicoResponseDto>> registrarDato(
            @Valid @RequestBody DatoMeteorologicoRequestDto dto) {
        DatoMeteorologicoResponseDto registrado = datoMeteorologicoService.registrarDato(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Dato meteorológico registrado exitosamente", registrado));
    }

    @GetMapping("/parcela/{parcelaId}")
    public ResponseEntity<ApiResponse<List<DatoMeteorologicoResponseDto>>> obtenerHistorialPorParcela(
            @PathVariable Long parcelaId) {
        List<DatoMeteorologicoResponseDto> historial = datoMeteorologicoService.obtenerHistorialPorParcela(parcelaId);
        return ResponseEntity.ok(ApiResponse.ok("Historial meteorológico obtenido exitosamente", historial));
    }

    @GetMapping("/parcela/{parcelaId}/ultimo")
    public ResponseEntity<ApiResponse<DatoMeteorologicoResponseDto>> obtenerUltimoDatoPorParcela(
            @PathVariable Long parcelaId) {
        DatoMeteorologicoResponseDto ultimoDato = datoMeteorologicoService.obtenerUltimoDatoPorParcela(parcelaId);
        return ResponseEntity.ok(ApiResponse.ok("Último dato meteorológico obtenido exitosamente", ultimoDato));
    }
}
