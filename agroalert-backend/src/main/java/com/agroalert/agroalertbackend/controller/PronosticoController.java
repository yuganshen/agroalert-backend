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
        if(response==null || response.hourly()==null) throw new ValidationException("El proveedor no devolvió pronóstico");
        Horas h=response.hourly(); List<DatoMeteorologicoResponseDto> result=new ArrayList<>();
        for(int i=0;i<h.time().size();i++) result.add(DatoMeteorologicoResponseDto.builder().parcelaId(id).parcelaNombre(parcela.getNombre()).fechaHora(LocalDateTime.parse(h.time().get(i))).temperatura(h.temperature_2m().get(i)).precipitacion(h.precipitation().get(i)).humedadRelativa(h.relative_humidity_2m().get(i)).velocidadViento(h.wind_speed_10m().get(i)).tipoDato(com.agroalert.agroalertbackend.entity.enums.TipoDato.PRONOSTICO).fuente("Open-Meteo").build());
        return ApiResponse.ok("Pronóstico de 24 horas",result);
    }
}
