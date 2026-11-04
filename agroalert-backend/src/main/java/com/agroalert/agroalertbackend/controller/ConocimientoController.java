// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/ConocimientoController.java
package com.agroalert.agroalertbackend.controller;
import com.agroalert.agroalertbackend.dto.*;
import com.agroalert.agroalertbackend.service.ConocimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
@RestController
@RequestMapping("/api/conocimiento")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TECNICO','ADMINISTRADOR')")
public class ConocimientoController {
    private final ConocimientoService service;
    @GetMapping("/{tipo}") public ApiResponse<List<ContenidoDto>> listar(@PathVariable String tipo) { return ApiResponse.ok("Contenido",service.listar(tipo)); }
    @PostMapping("/{tipo}") @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<ContenidoDto> crear(@PathVariable String tipo,@RequestBody ContenidoDto d) { return ApiResponse.ok("Creado",service.guardar(tipo,null,d)); }
    @PutMapping("/{tipo}/{id}") @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<ContenidoDto> editar(@PathVariable String tipo,@PathVariable Long id,@RequestBody ContenidoDto d) { return ApiResponse.ok("Actualizado; pendiente de validación",service.guardar(tipo,id,d)); }
    @PatchMapping("/{tipo}/{id}/validacion") @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<Void> validar(@PathVariable String tipo,@PathVariable Long id,@RequestBody ContenidoDto d) { service.cambiarEstado(tipo,id,d.getEstado(),d.getComentarioValidacion(),false); return ApiResponse.ok("Validación registrada"); }
    @DeleteMapping("/{tipo}/{id}") @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<Void> desactivar(@PathVariable String tipo,@PathVariable Long id) { service.cambiarEstado(tipo,id,null,null,true); return ApiResponse.ok("Desactivado"); }
}
