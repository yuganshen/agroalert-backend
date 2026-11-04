// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/PasoProcedimientoRepository.java
package com.agroalert.agroalertbackend.repository;
import com.agroalert.agroalertbackend.entity.PasoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PasoProcedimientoRepository extends JpaRepository<PasoProcedimiento, Long> {
    List<PasoProcedimiento> findByProcedimientoIdOrderByOrdenAsc(Long id);
    void deleteByProcedimientoId(Long id);
}
