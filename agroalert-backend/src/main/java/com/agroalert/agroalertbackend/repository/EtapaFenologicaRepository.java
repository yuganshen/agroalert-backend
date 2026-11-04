// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/EtapaFenologicaRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtapaFenologicaRepository extends JpaRepository<EtapaFenologica, Long> {

    @Query("SELECT e FROM EtapaFenologica e WHERE (e.activo = true OR e.activo IS NULL) AND e.estado = com.agroalert.agroalertbackend.entity.enums.EstadoValidacion.VALIDADO AND :dias >= e.diasDesdeMin AND :dias <= e.diasDesdeMax")
    Optional<EtapaFenologica> findEtapaPorDias(@Param("dias") Integer dias);
}
