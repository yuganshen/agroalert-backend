// Ruta: src/test/java/com/agroalert/agroalertbackend/scheduler/SincronizacionClimaticaSchedulerTest.java
package com.agroalert.agroalertbackend.scheduler;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoResponseDto;
import com.agroalert.agroalertbackend.dto.SincronizacionResultadoDto;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import com.agroalert.agroalertbackend.service.AlertaService;
import com.agroalert.agroalertbackend.service.ClimaExternoService;
import com.agroalert.agroalertbackend.service.DatoMeteorologicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SincronizacionClimaticaSchedulerTest {

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private ClimaExternoService climaExternoService;

    @Mock
    private DatoMeteorologicoService datoMeteorologicoService;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private SincronizacionClimaticaScheduler scheduler;

    private Parcela parcelaValida;
    private Parcela parcelaSinCoordenadas;
    private Comunidad comunidadValida;
    private DatoMeteorologicoRequestDto datoClimatico;

    @BeforeEach
    void setUp() {
        comunidadValida = Comunidad.builder()
                .id(1L)
                .nombre("Valle Camaná")
                .latitud(-16.62)
                .longitud(-72.71)
                .build();

        parcelaValida = Parcela.builder()
                .id(10L)
                .nombre("Parcela 1")
                .comunidad(comunidadValida)
                .activa(true)
                .build();

        parcelaSinCoordenadas = Parcela.builder()
                .id(20L)
                .nombre("Parcela 2")
                .comunidad(null)
                .activa(true)
                .build();

        datoClimatico = DatoMeteorologicoRequestDto.builder()
                .temperatura(25.0)
                .precipitacion(0.0)
                .humedadRelativa(70.0)
                .velocidadViento(10.0)
                .build();
    }

    @Test
    void testSincronizarYEvaluarParcelas_Exitoso() {
        when(parcelaRepository.findByActivaTrue()).thenReturn(List.of(parcelaValida));
        when(climaExternoService.consultarClimaActual(-16.62, -72.71)).thenReturn(datoClimatico);
        when(datoMeteorologicoService.registrarDato(any(DatoMeteorologicoRequestDto.class)))
                .thenReturn(DatoMeteorologicoResponseDto.builder().id(100L).build());
        when(alertaService.evaluarClimaYGenerarAlerta(10L))
                .thenReturn(List.of(AlertaResponseDto.builder().id(50L).build()));

        SincronizacionResultadoDto resultado = scheduler.sincronizarYEvaluarParcelas();

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalParcelasProcesadas());
        assertEquals(1, resultado.getParcelasSincronizadasExitosamente());
        assertEquals(0, resultado.getParcelasConError());
        assertEquals(1, resultado.getTotalAlertasGeneradas());

        verify(climaExternoService, times(1)).consultarClimaActual(-16.62, -72.71);
        verify(datoMeteorologicoService, times(1)).registrarDato(any(DatoMeteorologicoRequestDto.class));
        verify(alertaService, times(1)).evaluarClimaYGenerarAlerta(10L);
    }

    @Test
    void testSincronizarYEvaluarParcelas_SinComunidadOmitida() {
        when(parcelaRepository.findByActivaTrue()).thenReturn(List.of(parcelaSinCoordenadas));

        SincronizacionResultadoDto resultado = scheduler.sincronizarYEvaluarParcelas();

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalParcelasProcesadas());
        assertEquals(0, resultado.getParcelasSincronizadasExitosamente());
        assertEquals(1, resultado.getParcelasConError());

        verify(climaExternoService, never()).consultarClimaActual(any(), any());
        verify(datoMeteorologicoService, never()).registrarDato(any());
        verify(alertaService, never()).evaluarClimaYGenerarAlerta(any());
    }

    @Test
    void testSincronizarYEvaluarParcelas_ManejoDeExcepcionEnParcela() {
        when(parcelaRepository.findByActivaTrue()).thenReturn(List.of(parcelaValida));
        when(climaExternoService.consultarClimaActual(-16.62, -72.71))
                .thenThrow(new RuntimeException("API Gateway error"));

        SincronizacionResultadoDto resultado = scheduler.sincronizarYEvaluarParcelas();

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalParcelasProcesadas());
        assertEquals(0, resultado.getParcelasSincronizadasExitosamente());
        assertEquals(1, resultado.getParcelasConError());

        verify(datoMeteorologicoService, never()).registrarDato(any());
        verify(alertaService, never()).evaluarClimaYGenerarAlerta(any());
    }
}
