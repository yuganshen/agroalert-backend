// Ruta: src/test/java/com/agroalert/agroalertbackend/service/UsuarioServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.Administrador;
import com.agroalert.agroalertbackend.entity.Rol;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Rol rolAdmin = Rol.builder()
                .id(1L)
                .nombre(RolNombre.ADMINISTRADOR)
                .descripcion("Administrador del sistema")
                .build();

        usuario = Administrador.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@agroalert.com")
                .passwordHash("hashed_password")
                .activo(true)
                .roles(Set.of(rolAdmin))
                .build();
    }

    @Test
    void testListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDto> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        UsuarioResponseDto dto = resultado.get(0);
        assertEquals(usuario.getId(), dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Pérez", dto.getApellido());
        assertEquals("juan.perez@agroalert.com", dto.getEmail());
        assertTrue(dto.getActivo());
        assertTrue(dto.getRoles().contains("ADMINISTRADOR"));
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testCambiarEstadoUsuario_DeActivoAInactivo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDto resultado = usuarioService.cambiarEstadoUsuario(1L);

        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCambiarEstadoUsuario_DeInactivoAActivo() {
        usuario.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDto resultado = usuarioService.cambiarEstadoUsuario(1L);

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCambiarEstadoUsuario_NoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.cambiarEstadoUsuario(99L));
        verify(usuarioRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).save(any());
    }
}
