// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/ParcelaController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.ParcelaRequestDto;
import com.agroalert.agroalertbackend.dto.ParcelaResponseDto;
import com.agroalert.agroalertbackend.service.ParcelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcelas")
@RequiredArgsConstructor
public class ParcelaController {

    private final ParcelaService parcelaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('AGRICULTOR', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ParcelaResponseDto>> registrarParcela(@Valid @RequestBody ParcelaRequestDto dto) {
        ParcelaResponseDto parcelaCreada = parcelaService.registrarParcela(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Parcela registrada exitosamente", parcelaCreada));
    }

    @GetMapping("/agricultor/{agricultorId}")
    public ResponseEntity<ApiResponse<List<ParcelaResponseDto>>> listarParcelasPorAgricultor(@PathVariable Long agricultorId) {
        List<ParcelaResponseDto> parcelas = parcelaService.listarParcelasPorAgricultor(agricultorId);
        return ResponseEntity.ok(ApiResponse.ok("Parcelas del agricultor obtenidas exitosamente", parcelas));
    }

    @GetMapping("/comunidad/{comunidadId}")
    public ResponseEntity<ApiResponse<List<ParcelaResponseDto>>> listarParcelasPorComunidad(@PathVariable Long comunidadId) {
        List<ParcelaResponseDto> parcelas = parcelaService.listarParcelasPorComunidad(comunidadId);
        return ResponseEntity.ok(ApiResponse.ok("Parcelas de la comunidad obtenidas exitosamente", parcelas));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRICULTOR', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ParcelaResponseDto>> actualizarParcela(
            @PathVariable Long id,
            @Valid @RequestBody ParcelaRequestDto dto) {
        ParcelaResponseDto parcelaActualizada = parcelaService.actualizarParcela(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Parcela actualizada exitosamente", parcelaActualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRICULTOR', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivarParcela(@PathVariable Long id) {
        parcelaService.desactivarParcela(id);
        return ResponseEntity.ok(ApiResponse.ok("Parcela desactivada exitosamente"));
    }
}
