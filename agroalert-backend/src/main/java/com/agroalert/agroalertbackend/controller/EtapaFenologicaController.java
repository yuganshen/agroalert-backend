// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/EtapaFenologicaController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.EtapaFenologicaDto;
import com.agroalert.agroalertbackend.service.EtapaFenologicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/etapas-fenologicas")
@RequiredArgsConstructor
public class EtapaFenologicaController {

    private final EtapaFenologicaService etapaFenologicaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EtapaFenologicaDto>>> listarEtapas() {
        List<EtapaFenologicaDto> etapas = etapaFenologicaService.listarEtapas();
        return ResponseEntity.ok(ApiResponse.ok("Lista de etapas fenológicas obtenida exitosamente", etapas));
    }
}
