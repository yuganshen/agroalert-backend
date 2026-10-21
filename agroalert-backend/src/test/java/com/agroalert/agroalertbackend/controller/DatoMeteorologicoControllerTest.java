// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/DatoMeteorologicoControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoResponseDto;
import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.service.DatoMeteorologicoService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DatoMeteorologicoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DatoMeteorologicoService datoMeteorologicoService;

    @InjectMocks
    private DatoMeteorologicoController datoMeteorologicoController;

    private DatoMeteorologicoRequestDto requestDto;
    private DatoMeteorologicoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(datoMeteorologicoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        requestDto = DatoMeteorologicoRequestDto.builder()
                .parcelaId(1L)
                .fechaHora(LocalDateTime.of(2026, 10, 21, 12, 0))
                .temperatura(23.4)
                .precipitacion(0.0)
                .humedadRelativa(70.0)
                .velocidadViento(8.5)
                .tipoDato(TipoDato.ACTUAL)
                .fuente("SENAMHI")
                .build();

        responseDto = DatoMeteorologicoResponseDto.builder()
                .id(10L)
                .parcelaId(1L)
                .parcelaNombre("Parcela Test")
                .fechaHora(LocalDateTime.of(2026, 10, 21, 12, 0))
                .temperatura(23.4)
                .precipitacion(0.0)
                .humedadRelativa(70.0)
                .velocidadViento(8.5)
                .tipoDato(TipoDato.ACTUAL)
                .fuente("SENAMHI")
                .build();
    }

    @Test
    void testRegistrarDato_Exitoso() throws Exception {
        when(datoMeteorologicoService.registrarDato(any(DatoMeteorologicoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/datos-meteorologicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dato meteorológico registrado exitosamente"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.temperatura").value(23.4))
                .andExpect(jsonPath("$.data.tipoDato").value("ACTUAL"));

        verify(datoMeteorologicoService, times(1)).registrarDato(any(DatoMeteorologicoRequestDto.class));
    }

    @Test
    void testRegistrarDato_ValidacionFallida() throws Exception {
        DatoMeteorologicoRequestDto invalido = DatoMeteorologicoRequestDto.builder()
                .parcelaId(null)
                .temperatura(null)
                .precipitacion(null)
                .humedadRelativa(null)
                .velocidadViento(null)
                .build();

        mockMvc.perform(post("/api/datos-meteorologicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(datoMeteorologicoService, never()).registrarDato(any());
    }

    @Test
    void testObtenerHistorialPorParcela_Exitoso() throws Exception {
        when(datoMeteorologicoService.obtenerHistorialPorParcela(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/datos-meteorologicos/parcela/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Historial meteorológico obtenido exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].parcelaNombre").value("Parcela Test"));

        verify(datoMeteorologicoService, times(1)).obtenerHistorialPorParcela(1L);
    }

    @Test
    void testObtenerUltimoDatoPorParcela_Exitoso() throws Exception {
        when(datoMeteorologicoService.obtenerUltimoDatoPorParcela(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/datos-meteorologicos/parcela/1/ultimo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Último dato meteorológico obtenido exitosamente"))
                .andExpect(jsonPath("$.data.id").value(10));

        verify(datoMeteorologicoService, times(1)).obtenerUltimoDatoPorParcela(1L);
    }

    @Test
    void testObtenerUltimoDatoPorParcela_NoEncontrado() throws Exception {
        when(datoMeteorologicoService.obtenerUltimoDatoPorParcela(99L))
                .thenThrow(new ResourceNotFoundException("No se encontraron datos meteorológicos para la parcela con ID: 99"));

        mockMvc.perform(get("/api/datos-meteorologicos/parcela/99/ultimo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontraron datos meteorológicos para la parcela con ID: 99"));

        verify(datoMeteorologicoService, times(1)).obtenerUltimoDatoPorParcela(99L);
    }
}
