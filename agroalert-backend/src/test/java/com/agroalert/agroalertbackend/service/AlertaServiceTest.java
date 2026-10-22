// Ruta: src/test/java/com/agroalert/agroalertbackend/service/AlertaServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.entity.*;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private DatoMeteorologicoRepository datoMeteorologicoRepository;

    @Mock
    private ReglaDecisionRepository reglaDecisionRepository;

    @Mock
    private EtapaFenologicaRepository etapaFenologicaRepository;

    @Mock
    private MedidaRepository medidaRepository;

    @Mock
    private RecomendacionRepository recomendacionRepository;

    @Mock
    private ProcedimientoRepository procedimientoRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Parcela parcela;
    private DatoMeteorologico datoMeteorologico;
    private EtapaFenologica etapa;
    private Riesgo riesgo;
    private ReglaDecision regla;
    private Medida medida;
    private Recomendacion recomendacion;
    private Procedimiento procedimiento;
    private Alerta alerta;

    @BeforeEach
    void setUp() {
        parcela = Parcela.builder()
                .id(1L)
                .nombre("Parcela El Valle")
                .fechaSiembra(LocalDate.now().minusDays(30))
                .activa(true)
                .build();

        datoMeteorologico = DatoMeteorologico.builder()
                .id(10L)
                .parcela(parcela)
                .fechaHora(LocalDateTime.now())
                .temperatura(26.0)
                .precipitacion(15.0)
                .humedadRelativa(88.0)
                .velocidadViento(5.0)
                .build();

        etapa = EtapaFenologica.builder()
                .id(2L)
                .nombre("Macollamiento")
                .diasDesdeMin(20)
                .diasDesdeMax(40)
                .build();

        riesgo = Riesgo.builder()
                .id(3L)
                .nombre("Añublo del Arroz")
                .build();

        regla = ReglaDecision.builder()
                .id(4L)
                .variableMeteorologica("humedadRelativa")
                .operador(">")
                .umbral(80.0)
                .nivelResultante(NivelRiesgo.ALTO)
                .riesgo(riesgo)
                .etapaFenologica(etapa)
                .build();

        medida = Medida.builder()
                .id(5L)
                .nombre("Aplicación de fungicida preventivo")
                .riesgo(riesgo)
                .build();

        recomendacion = Recomendacion.builder()
                .id(6L)
                .texto("Reducir la lámina de agua y monitorear hojas")
                .medida(medida)
                .build();

        procedimiento = Procedimiento.builder()
                .id(7L)
                .titulo("Protocolo de control de Pyricularia")
                .medida(medida)
                .build();

        alerta = Alerta.builder()
                .id(100L)
                .parcela(parcela)
                .riesgo(riesgo)
                .reglaDecision(regla)
                .medida(medida)
                .nivel(NivelRiesgo.ALTO)
                .estado(EstadoAlerta.ACTIVA)
                .generadaEn(LocalDateTime.now())
                .condicionDetectada("humedadRelativa (88.00) > 80.00")
                .build();
    }

    @Test
    void testEvaluarClimaYGenerarAlerta_CondicionCumplida() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(Optional.of(datoMeteorologico));
        when(etapaFenologicaRepository.findEtapaPorDias(30)).thenReturn(Optional.of(etapa));
        when(reglaDecisionRepository.findByEtapaFenologicaId(2L)).thenReturn(List.of(regla));
        when(medidaRepository.findByRiesgoId(3L)).thenReturn(List.of(medida));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> {
            Alerta a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });
        when(recomendacionRepository.findByMedidaId(5L)).thenReturn(Optional.of(recomendacion));
        when(procedimientoRepository.findByMedidaId(5L)).thenReturn(Optional.of(procedimiento));

        List<AlertaResponseDto> resultado = alertaService.evaluarClimaYGenerarAlerta(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        AlertaResponseDto resAlerta = resultado.get(0);
        assertEquals(100L, resAlerta.getId());
        assertEquals(NivelRiesgo.ALTO, resAlerta.getNivel());
        assertEquals(EstadoAlerta.ACTIVA, resAlerta.getEstado());
        assertEquals(1L, resAlerta.getParcelaId());
        assertEquals("Parcela El Valle", resAlerta.getParcelaNombre());
        assertEquals("Añublo del Arroz", resAlerta.getRiesgoNombre());
        assertEquals("Aplicación de fungicida preventivo", resAlerta.getMedidaNombre());
        assertEquals("Reducir la lámina de agua y monitorear hojas", resAlerta.getRecomendacionTexto());
        assertEquals("Protocolo de control de Pyricularia", resAlerta.getProcedimientoTitulo());

        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void testEvaluarClimaYGenerarAlerta_CondicionNoCumplida() {
        regla.setUmbral(95.0); // Humedad actual es 88.0, umbral 95.0 no se cumple

        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(Optional.of(datoMeteorologico));
        when(etapaFenologicaRepository.findEtapaPorDias(30)).thenReturn(Optional.of(etapa));
        when(reglaDecisionRepository.findByEtapaFenologicaId(2L)).thenReturn(List.of(regla));

        List<AlertaResponseDto> resultado = alertaService.evaluarClimaYGenerarAlerta(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void testEvaluarClimaYGenerarAlerta_ParcelaNoEncontrada() {
        when(parcelaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.evaluarClimaYGenerarAlerta(99L));
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void testEvaluarClimaYGenerarAlerta_SinLecturaClimatica() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.evaluarClimaYGenerarAlerta(1L));
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void testListarAlertasPorParcela_Exitoso() {
        when(parcelaRepository.existsById(1L)).thenReturn(true);
        when(alertaRepository.findByParcelaIdOrderByGeneradaEnDesc(1L)).thenReturn(List.of(alerta));
        when(recomendacionRepository.findByMedidaId(5L)).thenReturn(Optional.of(recomendacion));
        when(procedimientoRepository.findByMedidaId(5L)).thenReturn(Optional.of(procedimiento));

        List<AlertaResponseDto> resultado = alertaService.listarAlertasPorParcela(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getId());
        verify(alertaRepository, times(1)).findByParcelaIdOrderByGeneradaEnDesc(1L);
    }

    @Test
    void testListarAlertasPorParcela_ParcelaNoExiste() {
        when(parcelaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> alertaService.listarAlertasPorParcela(99L));
        verify(alertaRepository, never()).findByParcelaIdOrderByGeneradaEnDesc(any());
    }

    @Test
    void testCambiarEstadoAlerta_Exitoso() {
        when(alertaRepository.findById(100L)).thenReturn(Optional.of(alerta));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertaResponseDto resultado = alertaService.cambiarEstadoAlerta(100L, EstadoAlerta.REVISADA);

        assertNotNull(resultado);
        assertEquals(EstadoAlerta.REVISADA, resultado.getEstado());
        verify(alertaRepository, times(1)).save(alerta);
    }

    @Test
    void testCambiarEstadoAlerta_NoEncontrada() {
        when(alertaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.cambiarEstadoAlerta(999L, EstadoAlerta.ARCHIVADA));
        verify(alertaRepository, never()).save(any());
    }
}
