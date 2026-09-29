// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/TokenJwtRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.TokenJWT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenJwtRepository extends JpaRepository<TokenJWT, Long> {

    Optional<TokenJWT> findByToken(String token);

    List<TokenJWT> findByUsuarioIdAndRevocadoFalse(Long usuarioId);
}
