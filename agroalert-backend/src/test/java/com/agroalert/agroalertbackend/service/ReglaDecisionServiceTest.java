// Ruta: src/test/java/com/agroalert/agroalertbackend/service/ReglaDecisionServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ReglaDecisionRequestDto;
import com.agroalert.agroalertbackend.dto.ReglaDecisionResponseDto;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.entity.ReglaDecision;
import com.agroalert.agroalertbackend.entity.Riesgo;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import com.agroalert.agroalertbackend.repository.ReglaDecisionRepository;
import com.agroalert.agroalertbackend.repository.RiesgoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReglaDecisionServiceTest {

    @Mock
    private ReglaDecisionRepository reglaDecisionRepository;

    @Mock
    private RiesgoRepository riesgoRepository;

    @Mock
    private EtapaFenologicaRepository etapaFenologicaRepository;

    @InjectMocks
    private ReglaDecisionService reglaDecisionService;

    private Riesgo riesgo;
    private EtapaFenologica etapa;
    private ReglaDecision regla;
    private ReglaDecisionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        riesgo = Riesgo.builder()
                .id(1L)
                .nombre("Piricularia")
                .descripcion("Hongo foliar")
                .build();

        etapa = EtapaFenologica.builder()
                .id(2L)
                .nombre("Macollamiento")
                .build();

        regla = ReglaDecision.builder()
                .id(10L)
                .variableMeteorologica("humedadRelativa")
                .operador(">")
                .umbral(85.0)
                .fuenteTecnica("Manual SENAMHI")
                .nivelResultante(NivelRiesgo.ALTO)
                .riesgo(riesgo)
                .etapaFenologica(etapa)
                .build();

        requestDto = ReglaDecisionRequestDto.builder()
                .variableMeteorologica("humedadRelativa")
                .operador(">")
                .umbral(85.0)
                .fuenteTecnica("Manual SENAMHI")
                .nivelResultante(NivelRiesgo.ALTO)
                .riesgoId(1L)
                .etapaFenologicaId(2L)
                .build();
    }

    @Test
    void testCrearRegla_Exitoso() {
        when(riesgoRepository.findById(1L)).thenReturn(Optional.of(riesgo));
        when(etapaFenologicaRepository.findById(2L)).thenReturn(Optional.of(etapa));
        when(reglaDecisionRepository.save(any(ReglaDecision.class))).thenAnswer(invocation -> {
            ReglaDecision r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReglaDecisionResponseDto resultado = reglaDecisionService.crearRegla(requestDto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("humedadRelativa", resultado.getVariableMeteorologica());
        assertEquals(">", resultado.getOperador());
        assertEquals(85.0, resultado.getUmbral());
        assertEquals(NivelRiesgo.ALTO, resultado.getNivelResultante());
        assertEquals("Piricularia", resultado.getRiesgoNombre());
        assertEquals("Macollamiento", resultado.getEtapaFenologicaNombre());

        verify(reglaDecisionRepository, times(1)).save(any(ReglaDecision.class));
    }

    @Test
    void testCrearRegla_RiesgoNoEncontrado() {
        when(riesgoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reglaDecisionService.crearRegla(requestDto));
        verify(reglaDecisionRepository, never()).save(any());
    }

    @Test
    void testCrearRegla_EtapaNoEncontrada() {
        when(riesgoRepository.findById(1L)).thenReturn(Optional.of(riesgo));
        when(etapaFenologicaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reglaDecisionService.crearRegla(requestDto));
        verify(reglaDecisionRepository, never()).save(any());
    }

    @Test
    void testListarReglas() {
        when(reglaDecisionRepository.findAll()).thenReturn(List.of(regla));

        List<ReglaDecisionResponseDto> resultado = reglaDecisionService.listarReglas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        verify(reglaDecisionRepository, times(1)).findAll();
    }

    @Test
    void testObtenerReglaPorId_Exitoso() {
        when(reglaDecisionRepository.findById(10L)).thenReturn(Optional.of(regla));

        ReglaDecisionResponseDto resultado = reglaDecisionService.obtenerReglaPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(reglaDecisionRepository, times(1)).findById(10L);
    }

    @Test
    void testObtenerReglaPorId_NoEncontrada() {
        when(reglaDecisionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reglaDecisionService.obtenerReglaPorId(99L));
        verify(reglaDecisionRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarRegla_Exitoso() {
        when(reglaDecisionRepository.findById(10L)).thenReturn(Optional.of(regla));
        when(riesgoRepository.findById(1L)).thenReturn(Optional.of(riesgo));
        when(etapaFenologicaRepository.findById(2L)).thenReturn(Optional.of(etapa));
        when(reglaDecisionRepository.save(any(ReglaDecision.class))).thenAnswer(invocation -> invocation.getArgument(0));

        requestDto.setUmbral(90.0);
        ReglaDecisionResponseDto resultado = reglaDecisionService.actualizarRegla(10L, requestDto);

        assertNotNull(resultado);
        assertEquals(90.0, resultado.getUmbral());
        verify(reglaDecisionRepository, times(1)).save(regla);
    }

    @Test
    void testEliminarRegla_Exitoso() {
        when(reglaDecisionRepository.existsById(10L)).thenReturn(true);
        doNothing().when(reglaDecisionRepository).deleteById(10L);

        reglaDecisionService.eliminarRegla(10L);

        verify(reglaDecisionRepository, times(1)).deleteById(10L);
    }

    @Test
    void testEliminarRegla_NoEncontrada() {
        when(reglaDecisionRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reglaDecisionService.eliminarRegla(99L));
        verify(reglaDecisionRepository, never()).deleteById(any());
    }
}
