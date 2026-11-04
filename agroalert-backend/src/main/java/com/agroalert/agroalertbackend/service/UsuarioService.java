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
    private final jakarta.persistence.EntityManager entityManager;
    private final com.agroalert.agroalertbackend.repository.TokenJwtRepository tokens;

    @Transactional
    public UsuarioResponseDto actualizar(Long id, com.agroalert.agroalertbackend.dto.ActualizarUsuarioDto d) {
        Usuario u=usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuarioRepository.findByEmail(d.getEmail()).ifPresent(other -> { if(!other.getId().equals(id)) throw new ValidationException("El correo ya está registrado"); });
        var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getName().equals(u.getEmail()) && !d.getRol().name().equals("ADMINISTRADOR")) throw new ValidationException("No puede quitarse su propio rol de administrador");
        String actual=u instanceof Agricultor ? "AGRICULTOR" : u instanceof TecnicoAgricola ? "TECNICO" : "ADMINISTRADOR";
        if(!actual.equals(d.getRol().name()) && u instanceof Agricultor) {
            Long parcelas=entityManager.createQuery("select count(p) from Parcela p where p.agricultor.id=:id",Long.class).setParameter("id",id).getSingleResult();
            if(parcelas>0) throw new ValidationException("Este agricultor tiene parcelas asociadas; conserve su rol para mantener el historial");
        }
        u.setNombre(d.getNombre());u.setApellido(d.getApellido());u.setEmail(d.getEmail());
        var rol=rolRepository.findByNombre(d.getRol()).orElseGet(() -> rolRepository.save(Rol.builder().nombre(d.getRol()).build()));
        u.setRoles(new java.util.HashSet<>(Set.of(rol)));
        if(u instanceof Agricultor a) { a.setDni(d.getDni());a.setTelefono(d.getTelefono()); }
        if(u instanceof TecnicoAgricola t) { t.setCodigoProfesional(d.getCodigoProfesional());t.setEspecialidad(d.getEspecialidad()); }
        usuarioRepository.saveAndFlush(u);
        if(!actual.equals(d.getRol().name())) {
            String tabla=actual.equals("AGRICULTOR") ? "agricultor" : actual.equals("TECNICO") ? "tecnico_agricola" : "administrador";
            entityManager.createNativeQuery("delete from "+tabla+" where id=:id").setParameter("id",id).executeUpdate();
            switch(d.getRol()) {
                case AGRICULTOR -> entityManager.createNativeQuery("insert into agricultor (id,dni,telefono) values (:id,:dni,:tel)").setParameter("id",id).setParameter("dni",d.getDni()).setParameter("tel",d.getTelefono()).executeUpdate();
                case TECNICO -> entityManager.createNativeQuery("insert into tecnico_agricola (id,codigo_profesional,especialidad) values (:id,:codigo,:esp)").setParameter("id",id).setParameter("codigo",d.getCodigoProfesional()).setParameter("esp",d.getEspecialidad()).executeUpdate();
                case ADMINISTRADOR -> entityManager.createNativeQuery("insert into administrador (id) values (:id)").setParameter("id",id).executeUpdate();
            }
        }
        var activos=tokens.findByUsuarioIdAndRevocadoFalse(id); activos.forEach(t -> t.setRevocado(true));tokens.saveAll(activos);
        entityManager.flush(); entityManager.clear();
        return mapToResponseDto(usuarioRepository.findById(id).orElseThrow());
    }


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

        var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getName().equals(usuario.getEmail())) throw new ValidationException("No puede desactivar su propia cuenta");
        Boolean nuevoEstado = !Boolean.TRUE.equals(usuario.getActivo());
        if(!nuevoEstado) { var activos=tokens.findByUsuarioIdAndRevocadoFalse(id); activos.forEach(t -> t.setRevocado(true)); tokens.saveAll(activos); }
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
