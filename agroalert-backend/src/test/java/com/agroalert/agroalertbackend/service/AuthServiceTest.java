// Ruta: src/test/java/com/agroalert/agroalertbackend/service/AuthServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.AuthResponse;
import com.agroalert.agroalertbackend.dto.LoginRequest;
import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Rol;
import com.agroalert.agroalertbackend.entity.TokenJWT;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.TokenJwtRepository;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import com.agroalert.agroalertbackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenJwtRepository tokenJwtRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Usuario usuario;
    private Rol rolAgricultor;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("agricultor@agroalert.com")
                .password("Password123!")
                .build();

        rolAgricultor = Rol.builder()
                .id(1L)
                .nombre(RolNombre.AGRICULTOR)
                .descripcion("Rol de agricultor")
                .build();

        usuario = Agricultor.builder()
                .id(10L)
                .nombre("Carlos")
                .apellido("Quispe")
                .email("agricultor@agroalert.com")
                .passwordHash("encoded-hash")
                .activo(true)
                .roles(Set.of(rolAgricultor))
                .dni("12345678")
                .telefono("987654321")
                .build();
    }

    @Test
    void testLogin_Exitoso() {
        String tokenGenerado = "mocked.jwt.token.string";
        Date fechaExpiracion = new Date(System.currentTimeMillis() + 3600000);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario))
                .thenReturn(tokenGenerado);
        when(tokenJwtRepository.findByUsuarioIdAndRevocadoFalse(10L))
                .thenReturn(List.of());
        when(jwtService.extractExpiration(tokenGenerado))
                .thenReturn(fechaExpiracion);
        when(tokenJwtRepository.save(any(TokenJWT.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(tokenGenerado, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("agricultor@agroalert.com", response.getEmail());
        assertNotNull(response.getRoles());
        assertTrue(response.getRoles().contains("AGRICULTOR"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtService, times(1)).generarToken(usuario);
        verify(tokenJwtRepository, times(1)).findByUsuarioIdAndRevocadoFalse(10L);
        verify(tokenJwtRepository, times(1)).save(any(TokenJWT.class));
    }

    @Test
    void testLogin_FalloCredencialesInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generarToken(any(Usuario.class));
        verify(tokenJwtRepository, never()).save(any());
    }

    @Test
    void testLogin_UsuarioNoEncontrado() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtService, never()).generarToken(any(Usuario.class));
        verify(tokenJwtRepository, never()).save(any());
    }

    @Test
    void testLogout_Exitoso() {
        String tokenStr = "mocked.jwt.token";
        TokenJWT tokenJWT = TokenJWT.builder()
                .id(1L)
                .token(tokenStr)
                .revocado(false)
                .emitidoEn(LocalDateTime.now())
                .expiraEn(LocalDateTime.now().plusHours(1))
                .usuario(usuario)
                .build();

        when(tokenJwtRepository.findByToken(tokenStr)).thenReturn(Optional.of(tokenJWT));
        when(tokenJwtRepository.save(any(TokenJWT.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.logout("Bearer " + tokenStr);

        assertTrue(tokenJWT.getRevocado());
        verify(tokenJwtRepository, times(1)).findByToken(tokenStr);
        verify(tokenJwtRepository, times(1)).save(tokenJWT);
    }
}

