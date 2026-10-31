// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/RolRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Rol;
import com.agroalert.agroalertbackend.entity.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(RolNombre nombre);
}
