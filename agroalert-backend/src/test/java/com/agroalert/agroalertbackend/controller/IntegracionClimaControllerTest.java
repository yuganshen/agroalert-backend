// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/IntegracionClimaControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.SincronizacionResultadoDto;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.scheduler.SincronizacionClimaticaScheduler;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IntegracionClimaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SincronizacionClimaticaScheduler sincronizacionClimaticaScheduler;

    @InjectMocks
    private IntegracionClimaController integracionClimaController;

    private SincronizacionResultadoDto resultadoDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(integracionClimaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        resultadoDto = SincronizacionResultadoDto.builder()
                .ejecutadoEn(LocalDateTime.of(2026, 10, 21, 20, 0))
                .totalParcelasProcesadas(2)
                .parcelasSincronizadasExitosamente(2)
                .parcelasConError(0)
                .totalAlertasGeneradas(1)
                .detalles(List.of("Parcela 1 sincronizada", "Parcela 2 sincronizada"))
                .build();
    }

    @Test
    void testSincronizarManual_Exitoso() throws Exception {
        when(sincronizacionClimaticaScheduler.sincronizarYEvaluarParcelas()).thenReturn(resultadoDto);

        mockMvc.perform(post("/api/integracion-clima/sincronizar-manual")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sincronización climática y evaluación ejecutada exitosamente"))
                .andExpect(jsonPath("$.data.totalParcelasProcesadas").value(2))
                .andExpect(jsonPath("$.data.parcelasSincronizadasExitosamente").value(2))
                .andExpect(jsonPath("$.data.totalAlertasGeneradas").value(1));

        verify(sincronizacionClimaticaScheduler, times(1)).sincronizarYEvaluarParcelas();
    }
}
