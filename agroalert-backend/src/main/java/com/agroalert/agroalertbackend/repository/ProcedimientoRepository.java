// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/ProcedimientoRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Procedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcedimientoRepository extends JpaRepository<Procedimiento, Long> {

    Optional<Procedimiento> findByMedidaId(Long medidaId);
}
