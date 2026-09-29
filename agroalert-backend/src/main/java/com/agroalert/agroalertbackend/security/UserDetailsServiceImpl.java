// Ruta: src/main/java/com/agroalert/agroalertbackend/security/UserDetailsServiceImpl.java
package com.agroalert.agroalertbackend.security;

import com.agroalert.agroalertbackend.entity.Usuario;
import com.agroalert.agroalertbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()))
                .collect(Collectors.toList());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .disabled(usuario.getActivo() != null && !usuario.getActivo())
                .authorities(authorities)
                .build();
    }
}
