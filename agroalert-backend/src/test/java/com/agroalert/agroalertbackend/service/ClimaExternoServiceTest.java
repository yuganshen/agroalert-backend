// Ruta: src/test/java/com/agroalert/agroalertbackend/service/ClimaExternoServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.external.OpenMeteoResponseDto;
import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimaExternoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClimaExternoService climaExternoService;

    private OpenMeteoResponseDto openMeteoResponseDto;

    @BeforeEach
    void setUp() {
        OpenMeteoResponseDto.Current current = OpenMeteoResponseDto.Current.builder()
                .time("2026-10-21T18:00")
                .temperature2m(22.8)
                .relativeHumidity2m(75.0)
                .precipitation(1.2)
                .windSpeed10m(14.5)
                .build();

        OpenMeteoResponseDto.CurrentWeather currentWeather = OpenMeteoResponseDto.CurrentWeather.builder()
                .temperature(22.8)
                .windspeed(14.5)
                .build();

        openMeteoResponseDto = OpenMeteoResponseDto.builder()
                .latitude(-16.62)
                .longitude(-72.71)
                .current(current)
                .currentWeather(currentWeather)
                .build();
    }

    @Test
    void testConsultarClimaActual_Exitoso() {
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponseDto.class)))
                .thenReturn(openMeteoResponseDto);

        DatoMeteorologicoRequestDto resultado = climaExternoService.consultarClimaActual(-16.62, -72.71);

        assertNotNull(resultado);
        assertEquals(22.8, resultado.getTemperatura());
        assertEquals(1.2, resultado.getPrecipitacion());
        assertEquals(75.0, resultado.getHumedadRelativa());
        assertEquals(14.5, resultado.getVelocidadViento());
        assertEquals(TipoDato.ACTUAL, resultado.getTipoDato());
        assertEquals("Open-Meteo", resultado.getFuente());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenMeteoResponseDto.class));
    }

    @Test
    void testConsultarClimaActual_CoordenadasNulas() {
        DatoMeteorologicoRequestDto resultado = climaExternoService.consultarClimaActual(null, -72.71);

        assertNull(resultado);
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void testConsultarClimaActual_RespuestaNula() {
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponseDto.class)))
                .thenReturn(null);

        DatoMeteorologicoRequestDto resultado = climaExternoService.consultarClimaActual(-16.62, -72.71);

        assertNull(resultado);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenMeteoResponseDto.class));
    }

    @Test
    void testConsultarClimaActual_ErrorRestTemplate() {
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponseDto.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        assertThrows(RestClientException.class, () -> climaExternoService.consultarClimaActual(-16.62, -72.71));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenMeteoResponseDto.class));
    }
}
