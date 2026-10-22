// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/ReglaDecisionControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.dto.ReglaDecisionRequestDto;
import com.agroalert.agroalertbackend.dto.ReglaDecisionResponseDto;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.service.ReglaDecisionService;
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
class ReglaDecisionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReglaDecisionService reglaDecisionService;

    @InjectMocks
    private ReglaDecisionController reglaDecisionController;

    private ReglaDecisionRequestDto requestDto;
    private ReglaDecisionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reglaDecisionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        requestDto = ReglaDecisionRequestDto.builder()
                .variableMeteorologica("humedadRelativa")
                .operador(">")
                .umbral(80.0)
                .fuenteTecnica("Manual SENAMHI")
                .nivelResultante(NivelRiesgo.ALTO)
                .riesgoId(1L)
                .etapaFenologicaId(2L)
                .build();

        responseDto = ReglaDecisionResponseDto.builder()
                .id(10L)
                .variableMeteorologica("humedadRelativa")
                .operador(">")
                .umbral(80.0)
                .fuenteTecnica("Manual SENAMHI")
                .nivelResultante(NivelRiesgo.ALTO)
                .riesgoNombre("Pyricularia")
                .etapaFenologicaNombre("Macollamiento")
                .build();
    }

    @Test
    void testCrearRegla_Exitoso() throws Exception {
        when(reglaDecisionService.crearRegla(any(ReglaDecisionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/reglas-decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Regla de decisión creada exitosamente"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.variableMeteorologica").value("humedadRelativa"));

        verify(reglaDecisionService, times(1)).crearRegla(any(ReglaDecisionRequestDto.class));
    }

    @Test
    void testCrearRegla_ValidacionFallida() throws Exception {
        ReglaDecisionRequestDto invalido = ReglaDecisionRequestDto.builder()
                .variableMeteorologica("")
                .operador("")
                .umbral(null)
                .nivelResultante(null)
                .riesgoId(null)
                .etapaFenologicaId(null)
                .build();

        mockMvc.perform(post("/api/reglas-decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(reglaDecisionService, never()).crearRegla(any());
    }

    @Test
    void testListarReglas() throws Exception {
        when(reglaDecisionService.listarReglas()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/reglas-decision")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lista de reglas de decisión obtenida exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(10));

        verify(reglaDecisionService, times(1)).listarReglas();
    }

    @Test
    void testObtenerReglaPorId() throws Exception {
        when(reglaDecisionService.obtenerReglaPorId(10L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/reglas-decision/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));

        verify(reglaDecisionService, times(1)).obtenerReglaPorId(10L);
    }

    @Test
    void testActualizarRegla() throws Exception {
        when(reglaDecisionService.actualizarRegla(eq(10L), any(ReglaDecisionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/reglas-decision/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Regla de decisión actualizada exitosamente"));

        verify(reglaDecisionService, times(1)).actualizarRegla(eq(10L), any(ReglaDecisionRequestDto.class));
    }

    @Test
    void testEliminarRegla() throws Exception {
        doNothing().when(reglaDecisionService).eliminarRegla(10L);

        mockMvc.perform(delete("/api/reglas-decision/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Regla de decisión eliminada exitosamente"));

        verify(reglaDecisionService, times(1)).eliminarRegla(10L);
    }
}
