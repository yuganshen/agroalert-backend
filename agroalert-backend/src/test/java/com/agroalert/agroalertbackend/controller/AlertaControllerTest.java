// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/AlertaControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.dto.CambiarEstadoAlertaDto;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.service.AlertaService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlertaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private AlertaController alertaController;

    private AlertaResponseDto alertaResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alertaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        alertaResponseDto = AlertaResponseDto.builder()
                .id(100L)
                .generadaEn(LocalDateTime.of(2026, 10, 21, 14, 0))
                .condicionDetectada("humedadRelativa (88.00) > 80.00")
                .nivel(NivelRiesgo.ALTO)
                .estado(EstadoAlerta.ACTIVA)
                .parcelaId(1L)
                .parcelaNombre("Parcela El Valle")
                .riesgoNombre("Pyricularia")
                .medidaNombre("Aplicación fungicida")
                .recomendacionTexto("Reducir lámina de agua")
                .procedimientoTitulo("Protocolo de control")
                .build();
    }

    @Test
    void testEvaluarClimaYGenerarAlerta_Exitoso() throws Exception {
        when(alertaService.evaluarClimaYGenerarAlerta(1L)).thenReturn(List.of(alertaResponseDto));

        mockMvc.perform(post("/api/alertas/evaluar/parcela/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].nivel").value("ALTO"))
                .andExpect(jsonPath("$.data[0].riesgoNombre").value("Pyricularia"));

        verify(alertaService, times(1)).evaluarClimaYGenerarAlerta(1L);
    }

    @Test
    void testListarAlertasPorParcela() throws Exception {
        when(alertaService.listarAlertasPorParcela(1L)).thenReturn(List.of(alertaResponseDto));

        mockMvc.perform(get("/api/alertas/parcela/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Alertas de la parcela obtenidas exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(100));

        verify(alertaService, times(1)).listarAlertasPorParcela(1L);
    }

    @Test
    void testCambiarEstadoAlerta_ConBody() throws Exception {
        CambiarEstadoAlertaDto dto = CambiarEstadoAlertaDto.builder()
                .estado(EstadoAlerta.REVISADA)
                .build();

        alertaResponseDto.setEstado(EstadoAlerta.REVISADA);
        when(alertaService.cambiarEstadoAlerta(eq(100L), eq(EstadoAlerta.REVISADA))).thenReturn(alertaResponseDto);

        mockMvc.perform(patch("/api/alertas/100/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.estado").value("REVISADA"));

        verify(alertaService, times(1)).cambiarEstadoAlerta(100L, EstadoAlerta.REVISADA);
    }

    @Test
    void testCambiarEstadoAlerta_ConParametro() throws Exception {
        alertaResponseDto.setEstado(EstadoAlerta.ARCHIVADA);
        when(alertaService.cambiarEstadoAlerta(eq(100L), eq(EstadoAlerta.ARCHIVADA))).thenReturn(alertaResponseDto);

        mockMvc.perform(patch("/api/alertas/100/estado")
                        .param("estado", "ARCHIVADA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.estado").value("ARCHIVADA"));

        verify(alertaService, times(1)).cambiarEstadoAlerta(100L, EstadoAlerta.ARCHIVADA);
    }
}
