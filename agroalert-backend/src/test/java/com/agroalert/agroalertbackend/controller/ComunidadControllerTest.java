// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/ComunidadControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ComunidadDto;
import com.agroalert.agroalertbackend.dto.ComunidadResponseDto;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.service.ComunidadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ComunidadControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ComunidadService comunidadService;

    @InjectMocks
    private ComunidadController comunidadController;

    private ComunidadDto comunidadDto;
    private ComunidadResponseDto comunidadResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(comunidadController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        comunidadDto = ComunidadDto.builder()
                .nombre("Valle de Camaná")
                .distrito("Camaná")
                .provincia("Camaná")
                .latitud(-16.6238)
                .longitud(-72.7111)
                .build();

        comunidadResponseDto = ComunidadResponseDto.builder()
                .id(1L)
                .nombre("Valle de Camaná")
                .distrito("Camaná")
                .provincia("Camaná")
                .latitud(-16.6238)
                .longitud(-72.7111)
                .activa(true)
                .build();
    }

    @Test
    void testCrearComunidad_Exitoso() throws Exception {
        when(comunidadService.crearComunidad(any(ComunidadDto.class))).thenReturn(comunidadResponseDto);

        mockMvc.perform(post("/api/comunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comunidadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comunidad creada exitosamente"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombre").value("Valle de Camaná"));

        verify(comunidadService, times(1)).crearComunidad(any(ComunidadDto.class));
    }

    @Test
    void testCrearComunidad_ValidacionInvalida() throws Exception {
        ComunidadDto invalidDto = ComunidadDto.builder()
                .nombre("")
                .distrito("")
                .provincia(null)
                .latitud(null)
                .longitud(null)
                .build();

        mockMvc.perform(post("/api/comunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(comunidadService, never()).crearComunidad(any());
    }

    @Test
    void testListarComunidades() throws Exception {
        when(comunidadService.listarComunidades()).thenReturn(List.of(comunidadResponseDto));

        mockMvc.perform(get("/api/comunidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lista de comunidades obtenida exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(comunidadService, times(1)).listarComunidades();
    }

    @Test
    void testActualizarComunidad_Exitoso() throws Exception {
        when(comunidadService.actualizarComunidad(eq(1L), any(ComunidadDto.class))).thenReturn(comunidadResponseDto);

        mockMvc.perform(put("/api/comunidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comunidadDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comunidad actualizada exitosamente"))
                .andExpect(jsonPath("$.data.nombre").value("Valle de Camaná"));

        verify(comunidadService, times(1)).actualizarComunidad(eq(1L), any(ComunidadDto.class));
    }

    @Test
    void testActualizarComunidad_NoEncontrada() throws Exception {
        when(comunidadService.actualizarComunidad(eq(99L), any(ComunidadDto.class)))
                .thenThrow(new ResourceNotFoundException("Comunidad no encontrada con ID: 99"));

        mockMvc.perform(put("/api/comunidades/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comunidadDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Comunidad no encontrada con ID: 99"));

        verify(comunidadService, times(1)).actualizarComunidad(eq(99L), any(ComunidadDto.class));
    }

    @Test
    void testEliminarComunidad_Exitoso() throws Exception {
        doNothing().when(comunidadService).eliminarComunidad(1L);

        mockMvc.perform(delete("/api/comunidades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comunidad desactivada exitosamente"));

        verify(comunidadService, times(1)).eliminarComunidad(1L);
    }
}
