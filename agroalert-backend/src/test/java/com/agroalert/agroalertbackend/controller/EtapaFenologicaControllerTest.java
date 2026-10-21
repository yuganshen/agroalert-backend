// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/EtapaFenologicaControllerTest.java
package com.agroalert.agroalertbackend.controller;

import com.agroalert.agroalertbackend.dto.EtapaFenologicaDto;
import com.agroalert.agroalertbackend.exception.GlobalExceptionHandler;
import com.agroalert.agroalertbackend.service.EtapaFenologicaService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EtapaFenologicaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EtapaFenologicaService etapaFenologicaService;

    @InjectMocks
    private EtapaFenologicaController etapaFenologicaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(etapaFenologicaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testListarEtapas() throws Exception {
        EtapaFenologicaDto etapaDto = EtapaFenologicaDto.builder()
                .id(1L)
                .nombre("Germinación")
                .descripcion("Fase inicial")
                .diasDesdeMin(0)
                .diasDesdeMax(10)
                .fuenteTecnica("Manual Técnico")
                .build();

        when(etapaFenologicaService.listarEtapas()).thenReturn(List.of(etapaDto));

        mockMvc.perform(get("/api/etapas-fenologicas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lista de etapas fenológicas obtenida exitosamente"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Germinación"));

        verify(etapaFenologicaService, times(1)).listarEtapas();
    }
}
