// Ruta: src/test/java/com/agroalert/agroalertbackend/repository/ParcelaRepositoryTest.java
package com.agroalert.agroalertbackend.repository;

import com.agroalert.agroalertbackend.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ParcelaRepositoryTest {
    @Autowired ParcelaRepository parcelas;
    @Autowired ComunidadRepository comunidades;

    // Deliberadamente sin @Transactional: reproduce el acceso del scheduler fuera de HTTP.
    @Test void comunidadDisponibleDespuesDeCerrarLaConsulta() {
        var comunidad=comunidades.save(Comunidad.builder().nombre("Dato de ejemplo scheduler")
                .distrito("Ejemplo").provincia("Ejemplo").latitud(-16.6).longitud(-72.7).activa(true).build());
        var parcela=parcelas.save(Parcela.builder().nombre("Dato de ejemplo scheduler").activa(true).comunidad(comunidad).build());
        try {
            var encontrada=parcelas.findByActivaTrue().stream().filter(p->p.getId().equals(parcela.getId())).findFirst().orElseThrow();
            assertTrue(org.hibernate.Hibernate.isInitialized(encontrada.getComunidad()));
            assertEquals(-16.6,encontrada.getComunidad().getLatitud());
        } finally {
            parcelas.deleteById(parcela.getId());comunidades.deleteById(comunidad.getId());
        }
    }
}
