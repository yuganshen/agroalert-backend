// Ruta: src/test/java/com/agroalert/agroalertbackend/service/EtapaFenologicaServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.EtapaFenologicaDto;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtapaFenologicaServiceTest {

    @Mock
    private EtapaFenologicaRepository etapaFenologicaRepository;

    @InjectMocks
    private EtapaFenologicaService etapaFenologicaService;

    @Test
    void testListarEtapas() {
        EtapaFenologica etapa = EtapaFenologica.builder()
                .id(1L)
                .nombre("Germinación")
                .descripcion("Fase inicial de brote")
                .diasDesdeMin(0)
                .diasDesdeMax(10)
                .fuenteTecnica("Manual Técnico Arroz")
                .build();

        when(etapaFenologicaRepository.findAll()).thenReturn(List.of(etapa));

        List<EtapaFenologicaDto> resultado = etapaFenologicaService.listarEtapas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Germinación", resultado.get(0).getNombre());
        assertEquals(0, resultado.get(0).getDiasDesdeMin());
        assertEquals(10, resultado.get(0).getDiasDesdeMax());
        verify(etapaFenologicaRepository, times(1)).findAll();
    }
}
