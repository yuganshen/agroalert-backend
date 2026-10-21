// Ruta: src/test/java/com/agroalert/agroalertbackend/service/ParcelaServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ParcelaRequestDto;
import com.agroalert.agroalertbackend.dto.ParcelaResponseDto;
import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.AgricultorRepository;
import com.agroalert.agroalertbackend.repository.ComunidadRepository;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelaServiceTest {

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private AgricultorRepository agricultorRepository;

    @Mock
    private ComunidadRepository comunidadRepository;

    @Mock
    private EtapaFenologicaRepository etapaFenologicaRepository;

    @InjectMocks
    private ParcelaService parcelaService;

    private Agricultor agricultor;
    private Comunidad comunidad;
    private Parcela parcela;
    private ParcelaRequestDto parcelaRequestDto;
    private EtapaFenologica etapa;

    @BeforeEach
    void setUp() {
        agricultor = Agricultor.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("Mendoza")
                .dni("12345678")
                .telefono("987654321")
                .build();

        comunidad = Comunidad.builder()
                .id(2L)
                .nombre("Valle de Camaná")
                .distrito("Camaná")
                .provincia("Camaná")
                .activa(true)
                .build();

        etapa = EtapaFenologica.builder()
                .id(1L)
                .nombre("Macollamiento")
                .diasDesdeMin(30)
                .diasDesdeMax(60)
                .build();

        parcela = Parcela.builder()
                .id(10L)
                .nombre("Parcela El Sol")
                .ubicacion("Sector La Punta")
                .areaHectareas(5.5)
                .fechaSiembra(LocalDate.now().minusDays(40))
                .activa(true)
                .agricultor(agricultor)
                .comunidad(comunidad)
                .build();

        parcelaRequestDto = ParcelaRequestDto.builder()
                .nombre("Parcela El Sol")
                .ubicacion("Sector La Punta")
                .areaHectareas(5.5)
                .fechaSiembra(LocalDate.now().minusDays(40))
                .comunidadId(2L)
                .agricultorId(1L)
                .build();
    }

    @Test
    void testRegistrarParcela_Exitoso() {
        when(agricultorRepository.findById(1L)).thenReturn(Optional.of(agricultor));
        when(comunidadRepository.findById(2L)).thenReturn(Optional.of(comunidad));
        when(parcelaRepository.save(any(Parcela.class))).thenAnswer(invocation -> {
            Parcela p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(etapaFenologicaRepository.findEtapaPorDias(40)).thenReturn(Optional.of(etapa));

        ParcelaResponseDto resultado = parcelaService.registrarParcela(parcelaRequestDto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Parcela El Sol", resultado.getNombre());
        assertEquals(5.5, resultado.getAreaHectareas());
        assertEquals("Valle de Camaná", resultado.getComunidadNombre());
        assertEquals("Carlos Mendoza", resultado.getAgricultorNombre());
        assertEquals("Macollamiento", resultado.getEtapaFenologicaNombre());
        assertTrue(resultado.getActiva());

        verify(parcelaRepository, times(1)).save(any(Parcela.class));
    }

    @Test
    void testRegistrarParcela_AgricultorNoEncontrado() {
        when(agricultorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelaService.registrarParcela(parcelaRequestDto));
        verify(parcelaRepository, never()).save(any());
    }

    @Test
    void testRegistrarParcela_ComunidadNoEncontrada() {
        when(agricultorRepository.findById(1L)).thenReturn(Optional.of(agricultor));
        when(comunidadRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelaService.registrarParcela(parcelaRequestDto));
        verify(parcelaRepository, never()).save(any());
    }

    @Test
    void testListarParcelasPorAgricultor() {
        when(parcelaRepository.findByAgricultorIdAndActivaTrue(1L)).thenReturn(List.of(parcela));
        when(etapaFenologicaRepository.findEtapaPorDias(40)).thenReturn(Optional.of(etapa));

        List<ParcelaResponseDto> resultado = parcelaService.listarParcelasPorAgricultor(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Parcela El Sol", resultado.get(0).getNombre());
        assertEquals("Macollamiento", resultado.get(0).getEtapaFenologicaNombre());
        verify(parcelaRepository, times(1)).findByAgricultorIdAndActivaTrue(1L);
    }

    @Test
    void testListarParcelasPorComunidad() {
        when(parcelaRepository.findByComunidadIdAndActivaTrue(2L)).thenReturn(List.of(parcela));
        when(etapaFenologicaRepository.findEtapaPorDias(40)).thenReturn(Optional.of(etapa));

        List<ParcelaResponseDto> resultado = parcelaService.listarParcelasPorComunidad(2L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Parcela El Sol", resultado.get(0).getNombre());
        verify(parcelaRepository, times(1)).findByComunidadIdAndActivaTrue(2L);
    }

    @Test
    void testActualizarParcela_Exitoso() {
        ParcelaRequestDto actualizacionDto = ParcelaRequestDto.builder()
                .nombre("Parcela La Luna")
                .ubicacion("Sector La Variante")
                .areaHectareas(7.0)
                .fechaSiembra(LocalDate.now().minusDays(10))
                .comunidadId(2L)
                .agricultorId(1L)
                .build();

        when(parcelaRepository.findById(10L)).thenReturn(Optional.of(parcela));
        when(agricultorRepository.findById(1L)).thenReturn(Optional.of(agricultor));
        when(comunidadRepository.findById(2L)).thenReturn(Optional.of(comunidad));
        when(parcelaRepository.save(any(Parcela.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParcelaResponseDto resultado = parcelaService.actualizarParcela(10L, actualizacionDto);

        assertNotNull(resultado);
        assertEquals("Parcela La Luna", resultado.getNombre());
        assertEquals("Sector La Variante", resultado.getUbicacion());
        assertEquals(7.0, resultado.getAreaHectareas());
        verify(parcelaRepository, times(1)).save(parcela);
    }

    @Test
    void testActualizarParcela_NoEncontrada() {
        when(parcelaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelaService.actualizarParcela(99L, parcelaRequestDto));
        verify(parcelaRepository, never()).save(any());
    }

    @Test
    void testDesactivarParcela_Exitoso() {
        when(parcelaRepository.findById(10L)).thenReturn(Optional.of(parcela));
        when(parcelaRepository.save(any(Parcela.class))).thenAnswer(invocation -> invocation.getArgument(0));

        parcelaService.desactivarParcela(10L);

        assertFalse(parcela.getActiva());
        verify(parcelaRepository, times(1)).findById(10L);
        verify(parcelaRepository, times(1)).save(parcela);
    }

    @Test
    void testDesactivarParcela_NoEncontrada() {
        when(parcelaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelaService.desactivarParcela(99L));
        verify(parcelaRepository, times(1)).findById(99L);
        verify(parcelaRepository, never()).save(any());
    }
}
