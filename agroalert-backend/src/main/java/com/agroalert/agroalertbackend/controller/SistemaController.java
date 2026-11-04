// Ruta: src/main/java/com/agroalert/agroalertbackend/controller/SistemaController.java
package com.agroalert.agroalertbackend.controller;
import com.agroalert.agroalertbackend.dto.ApiResponse;
import com.agroalert.agroalertbackend.scheduler.SincronizacionClimaticaScheduler;
import com.agroalert.agroalertbackend.repository.ReglaDecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;
import javax.sql.DataSource;
import java.util.*;
@RestController
@RequestMapping("/api/sistema")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class SistemaController {
    private final DataSource dataSource;
    private final SincronizacionClimaticaScheduler scheduler;
    private final ReglaDecisionRepository reglas;
    @Value("${clima.sincronizacion.cron:0 0 */1 * * *}") private String cron;
    @GetMapping("/estado") public ApiResponse<Map<String,Object>> estado() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("api","Disponible");
        boolean disponible=false;
        try(var connection=dataSource.getConnection()) { disponible=connection.isValid(2); } catch(Exception ignored) { }
        result.put("baseDatos",disponible ? "Disponible" : "Sin conexión");
        result.put("motorReglas",disponible ? reglas.findAll().stream().filter(r -> !Boolean.FALSE.equals(r.getActivo()) && r.getEstado()==com.agroalert.agroalertbackend.entity.enums.EstadoValidacion.VALIDADO).count()+" reglas validadas" : "No verificable");
        result.put("scheduler",cron.equals("-") ? "Deshabilitado" : "Programado: "+cron);
        var ultimo=scheduler.getUltimoResultado();
        result.put("fuenteMeteorologica",ultimo==null ? "Sin ejecución registrada desde el inicio" : ultimo.getParcelasConError()>0 ? "Última ejecución con errores" : ultimo.getTotalParcelasProcesadas()==0 ? "Sin parcelas para consultar" : "Última sincronización completada");
        result.put("ultimaSincronizacion",ultimo);
        return ApiResponse.ok("Estado del sistema",result);
    }
}
