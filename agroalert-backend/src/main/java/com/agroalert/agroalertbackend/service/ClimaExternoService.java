// Ruta: src/main/java/com/agroalert/agroalertbackend/service/ClimaExternoService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.external.OpenMeteoResponseDto;
import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClimaExternoService {

    private final RestTemplate restTemplate;

    public DatoMeteorologicoRequestDto consultarClimaActual(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            log.warn("Coordenadas no válidas para consulta climática: latitud={}, longitud={}", latitud, longitud);
            return null;
        }

        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&current_weather=true",
                latitud, longitud);

        log.info("Consultando clima externo en Open-Meteo: lat={}, lon={}", latitud, longitud);

        try {
            OpenMeteoResponseDto response = restTemplate.getForObject(url, OpenMeteoResponseDto.class);
            if (response == null) {
                log.warn("Respuesta vacía recibida desde Open-Meteo para lat={}, lon={}", latitud, longitud);
                return null;
            }

            Double temp = response.resolverTemperatura();
            Double precip = response.resolverPrecipitacion();
            Double humedad = response.resolverHumedadRelativa();
            Double viento = response.resolverVelocidadViento();

            log.info("Datos obtenidos de Open-Meteo: Temp={}°C, Precip={}mm, Humedad={}% , Viento={}km/h",
                    temp, precip, humedad, viento);

            return DatoMeteorologicoRequestDto.builder()
                    .fechaHora(LocalDateTime.now())
                    .temperatura(temp)
                    .precipitacion(precip)
                    .humedadRelativa(humedad)
                    .velocidadViento(viento)
                    .tipoDato(TipoDato.ACTUAL)
                    .fuente("Open-Meteo")
                    .build();

        } catch (RestClientException ex) {
            log.error("Error al consultar la API externa de Open-Meteo para lat={}, lon={}: {}",
                    latitud, longitud, ex.getMessage());
            throw ex;
        }
    }
}
