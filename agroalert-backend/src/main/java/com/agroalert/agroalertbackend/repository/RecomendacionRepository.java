// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/RecomendacionRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {

    Optional<Recomendacion> findByMedidaId(Long medidaId);
}
