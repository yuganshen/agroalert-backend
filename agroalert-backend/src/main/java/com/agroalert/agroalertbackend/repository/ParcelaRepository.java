// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/ParcelaRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    List<Parcela> findByAgricultorIdAndActivaTrue(Long agricultorId);

    List<Parcela> findByComunidadIdAndActivaTrue(Long comunidadId);
}
