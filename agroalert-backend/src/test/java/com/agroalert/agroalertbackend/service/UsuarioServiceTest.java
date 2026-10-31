// Ruta: src/test/java/com/agroalert/agroalertbackend/service/UsuarioServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.CrearRegistroUsuarioDto;
import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.Administrador;
import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Rol;
import com.agroalert.agroalertbackend.entity.TecnicoAgricola;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.exception.ValidationException;
import com.agroalert.agroalertbackend.repository.RolRepository;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Rol rolAdmin;
    private Rol rolAgricultor;
    private Rol rolTecnico;

    @BeforeEach
    void setUp() {
        rolAdmin = Rol.builder()
                .id(1L)
                .nombre(RolNombre.ADMINISTRADOR)
                .descripcion("Administrador del sistema")
                .build();

        rolAgricultor = Rol.builder()
                .id(2L)
                .nombre(RolNombre.AGRICULTOR)
                .descripcion("Agricultor")
                .build();

        rolTecnico = Rol.builder()
                .id(3L)
                .nombre(RolNombre.TECNICO)
                .descripcion("Técnico Agrícola")
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

    @Test
    void testCrearUsuario_Agricultor_Exitoso() {
        CrearRegistroUsuarioDto dto = CrearRegistroUsuarioDto.builder()
                .nombre("Carlos")
                .apellido("Quispe")
                .email("carlos.quispe@agroalert.com")
                .password("Password123!")
                .rol(RolNombre.AGRICULTOR)
                .dni("12345678")
                .telefono("987654321")
                .build();

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findByNombre(RolNombre.AGRICULTOR)).thenReturn(Optional.of(rolAgricultor));
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        UsuarioResponseDto resultado = usuarioService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("Quispe", resultado.getApellido());
        assertEquals("carlos.quispe@agroalert.com", resultado.getEmail());
        assertTrue(resultado.getActivo());
        assertTrue(resultado.getRoles().contains("AGRICULTOR"));
        assertEquals("12345678", resultado.getDni());
        assertEquals("987654321", resultado.getTelefono());

        verify(usuarioRepository, times(1)).existsByEmail(dto.getEmail());
        verify(rolRepository, times(1)).findByNombre(RolNombre.AGRICULTOR);
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(usuarioRepository, times(1)).save(any(Agricultor.class));
    }

    @Test
    void testCrearUsuario_Tecnico_Exitoso() {
        CrearRegistroUsuarioDto dto = CrearRegistroUsuarioDto.builder()
                .nombre("Ana")
                .apellido("Gómez")
                .email("ana.gomez@agroalert.com")
                .password("Password123!")
                .rol(RolNombre.TECNICO)
                .codigoProfesional("CIP-998877")
                .especialidad("Sanidad Vegetal")
                .build();

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findByNombre(RolNombre.TECNICO)).thenReturn(Optional.of(rolTecnico));
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(11L);
            return u;
        });

        UsuarioResponseDto resultado = usuarioService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals(11L, resultado.getId());
        assertEquals("Ana", resultado.getNombre());
        assertEquals("Gómez", resultado.getApellido());
        assertEquals("ana.gomez@agroalert.com", resultado.getEmail());
        assertTrue(resultado.getRoles().contains("TECNICO"));
        assertEquals("CIP-998877", resultado.getCodigoProfesional());
        assertEquals("Sanidad Vegetal", resultado.getEspecialidad());

        verify(usuarioRepository, times(1)).save(any(TecnicoAgricola.class));
    }

    @Test
    void testCrearUsuario_Administrador_Exitoso() {
        CrearRegistroUsuarioDto dto = CrearRegistroUsuarioDto.builder()
                .nombre("Mario")
                .apellido("Vargas")
                .email("mario@agroalert.com")
                .password("Password123!")
                .rol(RolNombre.ADMINISTRADOR)
                .build();

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findByNombre(RolNombre.ADMINISTRADOR)).thenReturn(Optional.of(rolAdmin));
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(12L);
            return u;
        });

        UsuarioResponseDto resultado = usuarioService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals(12L, resultado.getId());
        assertEquals("Mario", resultado.getNombre());
        assertTrue(resultado.getRoles().contains("ADMINISTRADOR"));

        verify(usuarioRepository, times(1)).save(any(Administrador.class));
    }

    @Test
    void testCrearUsuario_EmailYaRegistrado_LanzaValidationException() {
        CrearRegistroUsuarioDto dto = CrearRegistroUsuarioDto.builder()
                .nombre("Carlos")
                .apellido("Quispe")
                .email("carlos@agroalert.com")
                .password("Password123!")
                .rol(RolNombre.AGRICULTOR)
                .build();

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> usuarioService.crearUsuario(dto));
        assertTrue(ex.getMessage().contains("El correo electrónico ya está registrado"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testCrearUsuario_RolNull_LanzaValidationException() {
        CrearRegistroUsuarioDto dto = CrearRegistroUsuarioDto.builder()
                .nombre("Carlos")
                .apellido("Quispe")
                .email("carlos@agroalert.com")
                .password("Password123!")
                .rol(null)
                .build();

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        ValidationException ex = assertThrows(ValidationException.class, () -> usuarioService.crearUsuario(dto));
        assertTrue(ex.getMessage().contains("El rol es obligatorio"));

        verify(usuarioRepository, never()).save(any());
    }
}
