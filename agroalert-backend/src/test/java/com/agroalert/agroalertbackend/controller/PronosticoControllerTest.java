// Ruta: src/test/java/com/agroalert/agroalertbackend/controller/PronosticoControllerTest.java
package com.agroalert.agroalertbackend.controller;
import com.agroalert.agroalertbackend.entity.*;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PronosticoControllerTest {
    @Test void pronosticoIncompletoMantieneAusenciaEnVezDeInventar() {
        var repo=mock(ParcelaRepository.class);var rest=mock(RestTemplate.class);
        when(repo.findById(1L)).thenReturn(Optional.of(Parcela.builder().id(1L).nombre("Dato de ejemplo")
                .comunidad(Comunidad.builder().latitud(-16.6).longitud(-72.7).build()).build()));
        var horas=new PronosticoController.Horas(List.of("2026-11-03T12:00","2026-11-03T13:00"),List.of(20d),null,List.of(71d,72d),List.of(0d,1d));
        when(rest.getForObject(anyString(),eq(PronosticoController.Respuesta.class))).thenReturn(new PronosticoController.Respuesta(horas));
        var rows=new PronosticoController(repo,rest).obtener(1L).getData();
        assertEquals(2,rows.size());assertNull(rows.get(0).getPrecipitacion());assertNull(rows.get(1).getTemperatura());assertEquals(0d,rows.get(0).getVelocidadViento());
    }
    @Test void respuestaVaciaReportaErrorDeProveedor() {
        var repo=mock(ParcelaRepository.class);var rest=mock(RestTemplate.class);
        when(repo.findById(1L)).thenReturn(Optional.of(Parcela.builder().comunidad(Comunidad.builder().latitud(-16.6).longitud(-72.7).build()).build()));
        assertThrows(RestClientException.class,()->new PronosticoController(repo,rest).obtener(1L));
    }
}
