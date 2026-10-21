// Ruta: src/test/java/com/agroalert/agroalertbackend/service/ComunidadServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ComunidadDto;
import com.agroalert.agroalertbackend.dto.ComunidadResponseDto;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.ComunidadRepository;
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
class ComunidadServiceTest {

    @Mock
    private ComunidadRepository comunidadRepository;

    @InjectMocks
    private ComunidadService comunidadService;

    private Comunidad comunidad;
    private ComunidadDto comunidadDto;

    @BeforeEach
    void setUp() {
        comunidad = Comunidad.builder()
                .id(1L)
                .nombre("Valle de Camaná")
                .distrito("Camaná")
                .provincia("Camaná")
                .latitud(-16.6238)
                .longitud(-72.7111)
                .activa(true)
                .build();

        comunidadDto = ComunidadDto.builder()
                .nombre("Valle de Camaná")
                .distrito("Camaná")
                .provincia("Camaná")
                .latitud(-16.6238)
                .longitud(-72.7111)
                .build();
    }

    @Test
    void testCrearComunidad() {
        when(comunidadRepository.save(any(Comunidad.class))).thenAnswer(invocation -> {
            Comunidad c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        ComunidadResponseDto resultado = comunidadService.crearComunidad(comunidadDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Valle de Camaná", resultado.getNombre());
        assertTrue(resultado.getActiva());
        verify(comunidadRepository, times(1)).save(any(Comunidad.class));
    }

    @Test
    void testListarComunidades() {
        when(comunidadRepository.findByActivaTrue()).thenReturn(List.of(comunidad));

        List<ComunidadResponseDto> resultado = comunidadService.listarComunidades();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Valle de Camaná", resultado.get(0).getNombre());
        verify(comunidadRepository, times(1)).findByActivaTrue();
    }

    @Test
    void testActualizarComunidad_Exitoso() {
        ComunidadDto actualizacion = ComunidadDto.builder()
                .nombre("Nuevo Valle")
                .distrito("Samuel Pastor")
                .provincia("Camaná")
                .latitud(-16.6500)
                .longitud(-72.7000)
                .build();

        when(comunidadRepository.findById(1L)).thenReturn(Optional.of(comunidad));
        when(comunidadRepository.save(any(Comunidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComunidadResponseDto resultado = comunidadService.actualizarComunidad(1L, actualizacion);

        assertNotNull(resultado);
        assertEquals("Nuevo Valle", resultado.getNombre());
        assertEquals("Samuel Pastor", resultado.getDistrito());
        assertEquals(-16.6500, resultado.getLatitud());
        verify(comunidadRepository, times(1)).findById(1L);
        verify(comunidadRepository, times(1)).save(comunidad);
    }

    @Test
    void testActualizarComunidad_NoEncontrada() {
        when(comunidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comunidadService.actualizarComunidad(99L, comunidadDto));
        verify(comunidadRepository, times(1)).findById(99L);
        verify(comunidadRepository, never()).save(any());
    }

    @Test
    void testEliminarComunidad_Exitoso() {
        when(comunidadRepository.findById(1L)).thenReturn(Optional.of(comunidad));
        when(comunidadRepository.save(any(Comunidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        comunidadService.eliminarComunidad(1L);

        assertFalse(comunidad.getActiva());
        verify(comunidadRepository, times(1)).findById(1L);
        verify(comunidadRepository, times(1)).save(comunidad);
    }

    @Test
    void testEliminarComunidad_NoEncontrada() {
        when(comunidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comunidadService.eliminarComunidad(99L));
        verify(comunidadRepository, times(1)).findById(99L);
        verify(comunidadRepository, never()).save(any());
    }
}
