// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/MedidaRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Medida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedidaRepository extends JpaRepository<Medida, Long> {

    List<Medida> findByRiesgoId(Long riesgoId);
}
