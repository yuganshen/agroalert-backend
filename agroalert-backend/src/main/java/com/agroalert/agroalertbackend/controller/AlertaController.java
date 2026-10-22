// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/AlertaController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.CambiarEstadoAlertaDto;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping("/evaluar/parcela/{parcelaId}")
    public ResponseEntity<ApiResponse<List<AlertaResponseDto>>> evaluarClimaYGenerarAlerta(
            @PathVariable Long parcelaId) {
        List<AlertaResponseDto> alertas = alertaService.evaluarClimaYGenerarAlerta(parcelaId);
        String mensaje = alertas.isEmpty()
                ? "Evaluación completada. No se generaron nuevas alertas para la parcela."
                : "Evaluación completada. Se generaron " + alertas.size() + " alerta(s).";
        return ResponseEntity.ok(ApiResponse.ok(mensaje, alertas));
    }

    @GetMapping("/parcela/{parcelaId}")
    public ResponseEntity<ApiResponse<List<AlertaResponseDto>>> listarAlertasPorParcela(
            @PathVariable Long parcelaId) {
        List<AlertaResponseDto> alertas = alertaService.listarAlertasPorParcela(parcelaId);
        return ResponseEntity.ok(ApiResponse.ok("Alertas de la parcela obtenidas exitosamente", alertas));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<AlertaResponseDto>> cambiarEstadoAlerta(
            @PathVariable Long id,
            @RequestParam(required = false) EstadoAlerta estado,
            @RequestBody(required = false) CambiarEstadoAlertaDto dto) {
        EstadoAlerta nuevoEstado = EstadoAlerta.REVISADA;
        if (dto != null && dto.getEstado() != null) {
            nuevoEstado = dto.getEstado();
        } else if (estado != null) {
            nuevoEstado = estado;
        }

        AlertaResponseDto actualizada = alertaService.cambiarEstadoAlerta(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.ok("Estado de la alerta actualizado exitosamente", actualizada));
    }
}
