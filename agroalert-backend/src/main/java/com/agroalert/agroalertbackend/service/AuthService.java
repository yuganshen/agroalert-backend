// Ruta: src/main/java/com/agroalert/agroalertbackend/service/AuthService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.AuthResponse;
import com.agroalert.agroalertbackend.dto.LoginRequest;
import com.agroalert.agroalertbackend.entity.TokenJWT;
import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.TokenJwtRepository;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import com.agroalert.agroalertbackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenJwtRepository tokenJwtRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el email: " + request.getEmail()));

        String token = jwtService.generarToken(usuario);

        List<TokenJWT> tokensPrevios = tokenJwtRepository.findByUsuarioIdAndRevocadoFalse(usuario.getId());
        for (TokenJWT tokenPrevio : tokensPrevios) {
            tokenPrevio.setRevocado(true);
        }
        if (!tokensPrevios.isEmpty()) {
            tokenJwtRepository.saveAll(tokensPrevios);
        }

        Date expirationDate = jwtService.extractExpiration(token);
        LocalDateTime expiraEn = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        TokenJWT tokenJWT = TokenJWT.builder()
                .token(token)
                .emitidoEn(LocalDateTime.now())
                .expiraEn(expiraEn)
                .revocado(false)
                .usuario(usuario)
                .build();

        tokenJwtRepository.save(tokenJWT);

        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .toList();

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(usuario.getEmail())
                .roles(roles)
                .build();
    }

    @Transactional
    public void logout(String authHeaderToken) {
        if (authHeaderToken == null || authHeaderToken.isBlank()) {
            return;
        }

        String rawToken = authHeaderToken.startsWith("Bearer ")
                ? authHeaderToken.substring(7).trim()
                : authHeaderToken.trim();

        tokenJwtRepository.findByToken(rawToken).ifPresent(tokenJwt -> {
            tokenJwt.setRevocado(true);
            tokenJwtRepository.save(tokenJwt);
        });
    }
}
