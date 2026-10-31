// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/UsuarioControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.CrearRegistroUsuarioDto;
import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioResponseDto usuarioResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        usuarioResponseDto = UsuarioResponseDto.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@agroalert.com")
                .activo(true)
                .roles(List.of("ADMINISTRADOR"))
                .build();
    }

    @Test
    void testListarUsuarios() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioResponseDto));

        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lista de usuarios obtenida exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Juan"))
                .andExpect(jsonPath("$.data[0].roles[0]").value("ADMINISTRADOR"));

        verify(usuarioService, times(1)).listarUsuarios();
    }

    @Test
    void testCrearUsuario_Exitoso() throws Exception {
        CrearRegistroUsuarioDto requestDto = CrearRegistroUsuarioDto.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@agroalert.com")
                .password("Password123!")
                .rol(RolNombre.ADMINISTRADOR)
                .build();

        when(usuarioService.crearUsuario(any(CrearRegistroUsuarioDto.class))).thenReturn(usuarioResponseDto);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("juan@agroalert.com"));

        verify(usuarioService, times(1)).crearUsuario(any(CrearRegistroUsuarioDto.class));
    }

    @Test
    void testCrearUsuario_ValidacionFallida() throws Exception {
        CrearRegistroUsuarioDto requestDto = CrearRegistroUsuarioDto.builder()
                .nombre("")
                .apellido("")
                .email("email-invalido")
                .password("")
                .build();

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(usuarioService, never()).crearUsuario(any());
    }

    @Test
    void testCambiarEstadoUsuario_Exitoso() throws Exception {
        usuarioResponseDto.setActivo(false);
        when(usuarioService.cambiarEstadoUsuario(1L)).thenReturn(usuarioResponseDto);

        mockMvc.perform(patch("/api/usuarios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Estado de usuario cambiado exitosamente"))
                .andExpect(jsonPath("$.data.activo").value(false));

        verify(usuarioService, times(1)).cambiarEstadoUsuario(1L);
    }

    @Test
    void testCambiarEstadoUsuario_NoEncontrado() throws Exception {
        when(usuarioService.cambiarEstadoUsuario(99L))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 99"));

        mockMvc.perform(patch("/api/usuarios/99/estado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado con ID: 99"));

        verify(usuarioService, times(1)).cambiarEstadoUsuario(99L);
    }
}
