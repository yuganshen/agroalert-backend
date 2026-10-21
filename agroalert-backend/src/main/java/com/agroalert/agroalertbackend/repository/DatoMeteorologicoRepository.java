// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/DatoMeteorologicoRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.DatoMeteorologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatoMeteorologicoRepository extends JpaRepository<DatoMeteorologico, Long> {

    List<DatoMeteorologico> findByParcelaIdOrderByFechaHoraDesc(Long parcelaId);

    Optional<DatoMeteorologico> findTopByParcelaIdOrderByFechaHoraDesc(Long parcelaId);
}
