// Ruta: src/main/java/com/agroalert/agroalertbackend/service/UsuarioService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.CrearRegistroUsuarioDto;
import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.Administrador;
import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Rol;
import com.agroalert.agroalertbackend.entity.TecnicoAgricola;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.exception.ValidationException;
import com.agroalert.agroalertbackend.repository.RolRepository;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public UsuarioResponseDto crearUsuario(CrearRegistroUsuarioDto dto) {
        if (Boolean.TRUE.equals(usuarioRepository.existsByEmail(dto.getEmail()))) {
            throw new ValidationException("El correo electrónico ya está registrado: " + dto.getEmail());
        }

        if (dto.getRol() == null) {
            throw new ValidationException("El rol es obligatorio");
        }

        Rol rol = rolRepository.findByNombre(dto.getRol())
                .orElseGet(() -> rolRepository.save(
                        Rol.builder()
                                .nombre(dto.getRol())
                                .descripcion("Rol de " + dto.getRol().name())
                                .build()
                ));

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Set<Rol> roles = Set.of(rol);

        Usuario usuario = switch (dto.getRol()) {
            case AGRICULTOR -> Agricultor.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .email(dto.getEmail())
                    .passwordHash(encodedPassword)
                    .activo(true)
                    .creadoEn(LocalDateTime.now())
                    .roles(roles)
                    .dni(dto.getDni())
                    .telefono(dto.getTelefono())
                    .build();

            case TECNICO -> TecnicoAgricola.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .email(dto.getEmail())
                    .passwordHash(encodedPassword)
                    .activo(true)
                    .creadoEn(LocalDateTime.now())
                    .roles(roles)
                    .codigoProfesional(dto.getCodigoProfesional())
                    .especialidad(dto.getEspecialidad())
                    .build();

            case ADMINISTRADOR -> Administrador.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .email(dto.getEmail())
                    .passwordHash(encodedPassword)
                    .activo(true)
                    .creadoEn(LocalDateTime.now())
                    .roles(roles)
                    .build();
        };

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapToResponseDto(usuarioGuardado);
    }

    private UsuarioResponseDto mapToResponseDto(Usuario usuario) {
        List<String> roles = usuario.getRoles() != null
                ? usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList())
                : List.of();

        UsuarioResponseDto.UsuarioResponseDtoBuilder builder = UsuarioResponseDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .roles(roles);

        if (usuario instanceof Agricultor agricultor) {
            builder.dni(agricultor.getDni())
                    .telefono(agricultor.getTelefono());
        } else if (usuario instanceof TecnicoAgricola tecnico) {
            builder.codigoProfesional(tecnico.getCodigoProfesional())
                    .especialidad(tecnico.getEspecialidad());
        }

        return builder.build();
    }
}
