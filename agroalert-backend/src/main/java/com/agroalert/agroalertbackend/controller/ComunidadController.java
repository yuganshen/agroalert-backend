// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/ComunidadController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.ComunidadDto;
import com.agroalert.agroalertbackend.dto.ComunidadResponseDto;
import com.agroalert.agroalertbackend.service.ComunidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidades")
@RequiredArgsConstructor
public class ComunidadController {

    private final ComunidadService comunidadService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public ResponseEntity<ApiResponse<ComunidadResponseDto>> crearComunidad(@Valid @RequestBody ComunidadDto dto) {
        ComunidadResponseDto comunidadCreada = comunidadService.crearComunidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Comunidad creada exitosamente", comunidadCreada));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComunidadResponseDto>>> listarComunidades() {
        List<ComunidadResponseDto> comunidades = comunidadService.listarComunidades();
        return ResponseEntity.ok(ApiResponse.ok("Lista de comunidades obtenida exitosamente", comunidades));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ComunidadResponseDto>> actualizarComunidad(
            @PathVariable Long id,
            @Valid @RequestBody ComunidadDto dto) {
        ComunidadResponseDto comunidadActualizada = comunidadService.actualizarComunidad(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Comunidad actualizada exitosamente", comunidadActualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarComunidad(@PathVariable Long id) {
        comunidadService.eliminarComunidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Comunidad desactivada exitosamente"));
    }
}
