// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/ReglaDecisionController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.ReglaDecisionRequestDto;
import com.agroalert.agroalertbackend.dto.ReglaDecisionResponseDto;
import com.agroalert.agroalertbackend.service.ReglaDecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reglas-decision")
@RequiredArgsConstructor
public class ReglaDecisionController {

    private final ReglaDecisionService reglaDecisionService;

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<ApiResponse<ReglaDecisionResponseDto>> crearRegla(
            @Valid @RequestBody ReglaDecisionRequestDto dto) {
        ReglaDecisionResponseDto creada = reglaDecisionService.crearRegla(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Regla de decisión creada exitosamente", creada));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReglaDecisionResponseDto>>> listarReglas() {
        List<ReglaDecisionResponseDto> reglas = reglaDecisionService.listarReglas();
        return ResponseEntity.ok(ApiResponse.ok("Lista de reglas de decisión obtenida exitosamente", reglas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReglaDecisionResponseDto>> obtenerReglaPorId(@PathVariable Long id) {
        ReglaDecisionResponseDto regla = reglaDecisionService.obtenerReglaPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Regla de decisión obtenida exitosamente", regla));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<ApiResponse<ReglaDecisionResponseDto>> actualizarRegla(
            @PathVariable Long id,
            @Valid @RequestBody ReglaDecisionRequestDto dto) {
        ReglaDecisionResponseDto actualizada = reglaDecisionService.actualizarRegla(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Regla de decisión actualizada exitosamente", actualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<ApiResponse<Void>> eliminarRegla(@PathVariable Long id) {
        reglaDecisionService.eliminarRegla(id);
        return ResponseEntity.ok(ApiResponse.ok("Regla de decisión eliminada exitosamente"));
    }
}
