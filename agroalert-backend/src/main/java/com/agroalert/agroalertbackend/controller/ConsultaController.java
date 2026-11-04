// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/ConsultaController.java
package com.agroalert.agroalertbackend.controller;
import com.agroalert.agroalertbackend.dto.*;
import com.agroalert.agroalertbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsultaController {
    private final AccesoService acceso;
    private final ParcelaService parcelas;
    private final AlertaService alertas;
    @GetMapping("/mis-parcelas") @PreAuthorize("hasRole('AGRICULTOR')")
    public ApiResponse<List<ParcelaResponseDto>> parcelas() { return ApiResponse.ok("Mis parcelas",parcelas.listarParcelasPorAgricultor(acceso.actual().getId())); }
    @GetMapping("/mis-alertas") @PreAuthorize("hasRole('AGRICULTOR')")
    public ApiResponse<List<AlertaResponseDto>> alertas() { return ApiResponse.ok("Mis alertas",alertas.propias(acceso.actual().getId())); }
    @GetMapping("/alertas/{id}") @PreAuthorize("@acceso.alerta(#id)")
    public ApiResponse<AlertaResponseDto> alerta(@PathVariable Long id) { return ApiResponse.ok("Detalle de alerta",alertas.obtener(id)); }
}
