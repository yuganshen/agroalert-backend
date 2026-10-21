// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/ParcelaControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ParcelaRequestDto;
import com.agroalert.agroalertbackend.dto.ParcelaResponseDto;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.service.ParcelaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParcelaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ParcelaService parcelaService;

    @InjectMocks
    private ParcelaController parcelaController;

    private ParcelaRequestDto parcelaRequestDto;
    private ParcelaResponseDto parcelaResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parcelaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        parcelaRequestDto = ParcelaRequestDto.builder()
                .nombre("Parcela El Sol")
                .ubicacion("Valle Camaná Km 12")
                .areaHectareas(4.5)
                .fechaSiembra(LocalDate.of(2026, 1, 15))
                .comunidadId(1L)
                .agricultorId(2L)
                .build();

        parcelaResponseDto = ParcelaResponseDto.builder()
                .id(10L)
                .nombre("Parcela El Sol")
                .ubicacion("Valle Camaná Km 12")
                .areaHectareas(4.5)
                .fechaSiembra(LocalDate.of(2026, 1, 15))
                .activa(true)
                .comunidadNombre("Camaná Centro")
                .agricultorNombre("Juan Pérez")
                .etapaFenologicaNombre("Floración")
                .build();
    }

    @Test
    void testRegistrarParcela_Exitoso() throws Exception {
        when(parcelaService.registrarParcela(any(ParcelaRequestDto.class))).thenReturn(parcelaResponseDto);

        mockMvc.perform(post("/api/parcelas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelaRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parcela registrada exitosamente"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.nombre").value("Parcela El Sol"))
                .andExpect(jsonPath("$.data.etapaFenologicaNombre").value("Floración"));

        verify(parcelaService, times(1)).registrarParcela(any(ParcelaRequestDto.class));
    }

    @Test
    void testRegistrarParcela_ValidacionFallida() throws Exception {
        ParcelaRequestDto invalido = ParcelaRequestDto.builder()
                .nombre("")
                .ubicacion("")
                .areaHectareas(-1.0)
                .fechaSiembra(null)
                .comunidadId(null)
                .agricultorId(null)
                .build();

        mockMvc.perform(post("/api/parcelas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(parcelaService, never()).registrarParcela(any());
    }

    @Test
    void testListarParcelasPorAgricultor() throws Exception {
        when(parcelaService.listarParcelasPorAgricultor(2L)).thenReturn(List.of(parcelaResponseDto));

        mockMvc.perform(get("/api/parcelas/agricultor/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].agricultorNombre").value("Juan Pérez"));

        verify(parcelaService, times(1)).listarParcelasPorAgricultor(2L);
    }

    @Test
    void testListarParcelasPorComunidad() throws Exception {
        when(parcelaService.listarParcelasPorComunidad(1L)).thenReturn(List.of(parcelaResponseDto));

        mockMvc.perform(get("/api/parcelas/comunidad/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].comunidadNombre").value("Camaná Centro"));

        verify(parcelaService, times(1)).listarParcelasPorComunidad(1L);
    }

    @Test
    void testActualizarParcela_Exitoso() throws Exception {
        when(parcelaService.actualizarParcela(eq(10L), any(ParcelaRequestDto.class))).thenReturn(parcelaResponseDto);

        mockMvc.perform(put("/api/parcelas/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelaRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parcela actualizada exitosamente"))
                .andExpect(jsonPath("$.data.id").value(10));

        verify(parcelaService, times(1)).actualizarParcela(eq(10L), any(ParcelaRequestDto.class));
    }

    @Test
    void testActualizarParcela_NoEncontrada() throws Exception {
        when(parcelaService.actualizarParcela(eq(99L), any(ParcelaRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Parcela no encontrada con ID: 99"));

        mockMvc.perform(put("/api/parcelas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelaRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Parcela no encontrada con ID: 99"));

        verify(parcelaService, times(1)).actualizarParcela(eq(99L), any(ParcelaRequestDto.class));
    }

    @Test
    void testDesactivarParcela_Exitoso() throws Exception {
        doNothing().when(parcelaService).desactivarParcela(10L);

        mockMvc.perform(delete("/api/parcelas/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parcela desactivada exitosamente"));

        verify(parcelaService, times(1)).desactivarParcela(10L);
    }
}
