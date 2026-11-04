// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/PronosticoController.java
package com.agroalert.agroalertbackend.controller;
import com.agroalert.agroalertbackend.dto.*;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import com.agroalert.agroalertbackend.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/pronostico")
@RequiredArgsConstructor
public class PronosticoController {
    private final ParcelaRepository parcelas;
    private final RestTemplate restTemplate;
    public record Horas(List<String> time,List<Double> temperature_2m,List<Double> precipitation,List<Double> relative_humidity_2m,List<Double> wind_speed_10m) {}
    public record Respuesta(Horas hourly) {}
    @GetMapping("/parcela/{id}") @PreAuthorize("@acceso.parcela(#id)")
    public ApiResponse<List<DatoMeteorologicoResponseDto>> obtener(@PathVariable Long id) {
        var parcela=parcelas.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada"));
        var c=parcela.getComunidad();
        if(c==null || c.getLatitud()==null || c.getLongitud()==null) throw new ValidationException("La comunidad no tiene coordenadas");
        String url=String.format(Locale.US,"https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&hourly=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&forecast_hours=24&timezone=America/Lima",c.getLatitud(),c.getLongitud());
        Respuesta response=restTemplate.getForObject(url,Respuesta.class);
        if(response==null || response.hourly()==null || response.hourly().time()==null || response.hourly().time().isEmpty())
            throw new org.springframework.web.client.RestClientException("El proveedor no devolvió pronóstico");
        Horas h=response.hourly(); List<DatoMeteorologicoResponseDto> result=new ArrayList<>();
        try {
            for(int i=0;i<h.time().size();i++) result.add(DatoMeteorologicoResponseDto.builder().parcelaId(id).parcelaNombre(parcela.getNombre()).fechaHora(LocalDateTime.parse(h.time().get(i))).temperatura(valor(h.temperature_2m(),i)).precipitacion(valor(h.precipitation(),i)).humedadRelativa(valor(h.relative_humidity_2m(),i)).velocidadViento(valor(h.wind_speed_10m(),i)).tipoDato(com.agroalert.agroalertbackend.entity.enums.TipoDato.PRONOSTICO).fuente("Open-Meteo").build());
        } catch (java.time.format.DateTimeParseException | NullPointerException ex) {
            throw new org.springframework.web.client.RestClientException("El pronóstico no contiene horas válidas", ex);
        }
        return ApiResponse.ok("Pronóstico de 24 horas",result);
    }
    private Double valor(List<Double> datos, int indice) {
        if(datos==null || indice>=datos.size()) return null;
        Double dato=datos.get(indice);
        return dato!=null && Double.isFinite(dato) ? dato : null;
    }
}
