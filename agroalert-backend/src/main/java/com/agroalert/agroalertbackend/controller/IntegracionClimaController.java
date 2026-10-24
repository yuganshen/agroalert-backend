// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/IntegracionClimaController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.SincronizacionResultadoDto;
import com.agroalert.agroalertbackend.scheduler.SincronizacionClimaticaScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integracion-clima")
@RequiredArgsConstructor
public class IntegracionClimaController {

    private final SincronizacionClimaticaScheduler sincronizacionClimaticaScheduler;

    @PostMapping("/sincronizar-manual")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<SincronizacionResultadoDto>> sincronizarManual() {
        SincronizacionResultadoDto resultado = sincronizacionClimaticaScheduler.sincronizarYEvaluarParcelas();
        return ResponseEntity.ok(ApiResponse.ok("Sincronización climática y evaluación ejecutada exitosamente", resultado));
    }
}
