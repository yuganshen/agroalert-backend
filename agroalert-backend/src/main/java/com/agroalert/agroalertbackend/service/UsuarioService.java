// Ruta: src/main/java/com/agroalert/agroalertbackend/service/UsuarioService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDto cambiarEstadoUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        Boolean nuevoEstado = !Boolean.TRUE.equals(usuario.getActivo());
        usuario.setActivo(nuevoEstado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapToResponseDto(usuarioActualizado);
    }

    private UsuarioResponseDto mapToResponseDto(Usuario usuario) {
        List<String> roles = usuario.getRoles() != null
                ? usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList())
                : List.of();

        return UsuarioResponseDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .roles(roles)
                .build();
    }
}
