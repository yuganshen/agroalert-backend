// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/AlertaRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.Alerta;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByParcelaIdOrderByGeneradaEnDesc(Long parcelaId);

    List<Alerta> findByEstado(EstadoAlerta estado);
}
