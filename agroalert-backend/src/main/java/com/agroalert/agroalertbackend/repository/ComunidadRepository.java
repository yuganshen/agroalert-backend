// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/ComunidadRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Comunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunidadRepository extends JpaRepository<Comunidad, Long> {

    List<Comunidad> findByActivaTrue();
}
