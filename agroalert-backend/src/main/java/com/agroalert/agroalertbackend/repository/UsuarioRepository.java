// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/UsuarioRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);
}
