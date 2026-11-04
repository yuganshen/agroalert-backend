// Ruta: src/main/java/com/agroalert/agroalertbackend/service/AccesoService.java
package com.agroalert.agroalertbackend.service;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.repository.*;
import com.agroalert.agroalertbackend.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
@Service("acceso")
@RequiredArgsConstructor
public class AccesoService {
    private final UsuarioRepository usuarios;
    private final ParcelaRepository parcelas;
    private final AlertaRepository alertas;
    public Usuario actual() {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null) throw new AccessDeniedException("Inicie sesión");
        return usuarios.findByEmail(auth.getName()).filter(u -> Boolean.TRUE.equals(u.getActivo())).orElseThrow(() -> new AccessDeniedException("Usuario no disponible"));
    }
    public boolean agricultor(Long id) {
        var u=actual(); return u.getRoles().stream().anyMatch(r -> r.getNombre().name().equals("ADMINISTRADOR")) || u.getId().equals(id);
    }
    public boolean parcela(Long id) {
        var p=parcelas.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada"));
        var u=actual();
        return u.getRoles().stream().anyMatch(r -> !r.getNombre().name().equals("AGRICULTOR")) || p.getAgricultor().getId().equals(u.getId());
    }
    public boolean alerta(Long id) { return parcela(alertas.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada")).getParcela().getId()); }
}
