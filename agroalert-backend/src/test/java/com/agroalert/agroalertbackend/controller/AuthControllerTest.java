// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/AuthControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.AuthResponse;
import com.agroalert.agroalertbackend.dto.CrearRegistroUsuarioDto;
import com.agroalert.agroalertbackend.dto.LoginRequest;
import com.agroalert.agroalertbackend.dto.UsuarioResponseDto;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.service.AuthService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogin_Exitoso() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("agricultor@agroalert.com")
                .password("Password123!")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("mocked-jwt-token")
                .type("Bearer")
                .email("agricultor@agroalert.com")
                .roles(List.of("AGRICULTOR"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.email").value("agricultor@agroalert.com"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testRegistroAgricultor_Exitoso() throws Exception {
        CrearRegistroUsuarioDto registroDto = CrearRegistroUsuarioDto.builder()
                .nombre("Carlos")
                .apellido("Quispe")
                .email("carlos@agroalert.com")
                .password("Password123!")
                .dni("12345678")
                .telefono("987654321")
                .build();

        UsuarioResponseDto usuarioResponse = UsuarioResponseDto.builder()
                .id(5L)
                .nombre("Carlos")
                .apellido("Quispe")
                .email("carlos@agroalert.com")
                .activo(true)
                .roles(List.of("AGRICULTOR"))
                .dni("12345678")
                .telefono("987654321")
                .build();

        when(usuarioService.crearUsuario(any(CrearRegistroUsuarioDto.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Agricultor registrado exitosamente"))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.nombre").value("Carlos"))
                .andExpect(jsonPath("$.data.roles[0]").value("AGRICULTOR"))
                .andExpect(jsonPath("$.data.dni").value("12345678"));

        verify(usuarioService, times(1)).crearUsuario(argThat(dto ->
                dto.getRol() == RolNombre.AGRICULTOR &&
                "carlos@agroalert.com".equals(dto.getEmail())
        ));
    }

    @Test
    void testRegistroAgricultor_ValidacionFallida() throws Exception {
        CrearRegistroUsuarioDto requestDto = CrearRegistroUsuarioDto.builder()
                .nombre("")
                .apellido("")
                .email("email-invalido")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(usuarioService, never()).crearUsuario(any());
    }

    @Test
    void testLogout_Exitoso() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer mocked-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cierre de sesión exitoso"));

        verify(authService, times(1)).logout("Bearer mocked-jwt-token");
    }
}
