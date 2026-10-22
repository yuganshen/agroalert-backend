// Ruta: src/main/java/com/agroalert/agroalertbackend/repository/ReglaDecisionRepository.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.ReglaDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaDecisionRepository extends JpaRepository<ReglaDecision, Long> {

    List<ReglaDecision> findByEtapaFenologicaId(Long etapaId);
}
