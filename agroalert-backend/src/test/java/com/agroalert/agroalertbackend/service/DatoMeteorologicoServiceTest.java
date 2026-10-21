// Ruta: src/test/java/com/agroalert/agroalertbackend/service/DatoMeteorologicoServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoResponseDto;
import com.agroalert.agroalertbackend.entity.DatoMeteorologico;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.DatoMeteorologicoRepository;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatoMeteorologicoServiceTest {

    @Mock
    private DatoMeteorologicoRepository datoMeteorologicoRepository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @InjectMocks
    private DatoMeteorologicoService datoMeteorologicoService;

    private Parcela parcela;
    private DatoMeteorologico datoMeteorologico;
    private DatoMeteorologicoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        parcela = Parcela.builder()
                .id(1L)
                .nombre("Parcela San Isidro")
                .ubicacion("Camaná")
                .areaHectareas(10.0)
                .activa(true)
                .build();

        datoMeteorologico = DatoMeteorologico.builder()
                .id(100L)
                .parcela(parcela)
                .fechaHora(LocalDateTime.of(2026, 10, 21, 10, 0))
                .temperatura(24.5)
                .precipitacion(0.0)
                .humedadRelativa(65.0)
                .velocidadViento(12.3)
                .tipoDato(TipoDato.ACTUAL)
                .fuente("SENAMHI")
                .build();

        requestDto = DatoMeteorologicoRequestDto.builder()
                .parcelaId(1L)
                .fechaHora(LocalDateTime.of(2026, 10, 21, 10, 0))
                .temperatura(24.5)
                .precipitacion(0.0)
                .humedadRelativa(65.0)
                .velocidadViento(12.3)
                .tipoDato(TipoDato.ACTUAL)
                .fuente("SENAMHI")
                .build();
    }

    @Test
    void testRegistrarDato_Exitoso() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(datoMeteorologicoRepository.save(any(DatoMeteorologico.class))).thenAnswer(invocation -> {
            DatoMeteorologico d = invocation.getArgument(0);
            d.setId(100L);
            return d;
        });

        DatoMeteorologicoResponseDto resultado = datoMeteorologicoService.registrarDato(requestDto);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(1L, resultado.getParcelaId());
        assertEquals("Parcela San Isidro", resultado.getParcelaNombre());
        assertEquals(24.5, resultado.getTemperatura());
        assertEquals(TipoDato.ACTUAL, resultado.getTipoDato());
        assertEquals("SENAMHI", resultado.getFuente());

        verify(datoMeteorologicoRepository, times(1)).save(any(DatoMeteorologico.class));
    }

    @Test
    void testRegistrarDato_ValoresPorDefecto() {
        DatoMeteorologicoRequestDto dtoSinFechaYTipo = DatoMeteorologicoRequestDto.builder()
                .parcelaId(1L)
                .temperatura(20.0)
                .precipitacion(1.5)
                .humedadRelativa(80.0)
                .velocidadViento(5.0)
                .fuente("Open-Meteo")
                .build();

        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(datoMeteorologicoRepository.save(any(DatoMeteorologico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DatoMeteorologicoResponseDto resultado = datoMeteorologicoService.registrarDato(dtoSinFechaYTipo);

        assertNotNull(resultado);
        assertNotNull(resultado.getFechaHora());
        assertEquals(TipoDato.ACTUAL, resultado.getTipoDato());
    }

    @Test
    void testRegistrarDato_ParcelaNoEncontrada() {
        when(parcelaRepository.findById(99L)).thenReturn(Optional.empty());
        requestDto.setParcelaId(99L);

        assertThrows(ResourceNotFoundException.class, () -> datoMeteorologicoService.registrarDato(requestDto));
        verify(datoMeteorologicoRepository, never()).save(any());
    }

    @Test
    void testObtenerHistorialPorParcela_Exitoso() {
        when(parcelaRepository.existsById(1L)).thenReturn(true);
        when(datoMeteorologicoRepository.findByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(List.of(datoMeteorologico));

        List<DatoMeteorologicoResponseDto> resultado = datoMeteorologicoService.obtenerHistorialPorParcela(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getId());
        assertEquals(24.5, resultado.get(0).getTemperatura());
        verify(datoMeteorologicoRepository, times(1)).findByParcelaIdOrderByFechaHoraDesc(1L);
    }

    @Test
    void testObtenerHistorialPorParcela_ParcelaNoEncontrada() {
        when(parcelaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> datoMeteorologicoService.obtenerHistorialPorParcela(99L));
        verify(datoMeteorologicoRepository, never()).findByParcelaIdOrderByFechaHoraDesc(any());
    }

    @Test
    void testObtenerUltimoDatoPorParcela_Exitoso() {
        when(parcelaRepository.existsById(1L)).thenReturn(true);
        when(datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(Optional.of(datoMeteorologico));

        DatoMeteorologicoResponseDto resultado = datoMeteorologicoService.obtenerUltimoDatoPorParcela(1L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Parcela San Isidro", resultado.getParcelaNombre());
        verify(datoMeteorologicoRepository, times(1)).findTopByParcelaIdOrderByFechaHoraDesc(1L);
    }

    @Test
    void testObtenerUltimoDatoPorParcela_ParcelaNoEncontrada() {
        when(parcelaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> datoMeteorologicoService.obtenerUltimoDatoPorParcela(99L));
        verify(datoMeteorologicoRepository, never()).findTopByParcelaIdOrderByFechaHoraDesc(any());
    }

    @Test
    void testObtenerUltimoDatoPorParcela_SinDatos() {
        when(parcelaRepository.existsById(1L)).thenReturn(true);
        when(datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> datoMeteorologicoService.obtenerUltimoDatoPorParcela(1L));
        verify(datoMeteorologicoRepository, times(1)).findTopByParcelaIdOrderByFechaHoraDesc(1L);
    }
}
