// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/UsuarioController.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listarUsuarios() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(ApiResponse.ok("Lista de usuarios obtenida exitosamente", usuarios));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> cambiarEstadoUsuario(@PathVariable Long id) {
        UsuarioResponseDto usuarioActualizado = usuarioService.cambiarEstadoUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Estado de usuario cambiado exitosamente", usuarioActualizado));
    }
}
