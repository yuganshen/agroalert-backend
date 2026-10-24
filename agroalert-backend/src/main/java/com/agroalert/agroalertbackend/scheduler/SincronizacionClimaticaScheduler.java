// Ruta: src/main/java/com/agroalert/agroalertbackend/scheduler/SincronizacionClimaticaScheduler.java
package com.agroalert.agroalertbackend.scheduler;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.SincronizacionResultadoDto;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import com.agroalert.agroalertbackend.service.AlertaService;
import com.agroalert.agroalertbackend.service.ClimaExternoService;
import com.agroalert.agroalertbackend.service.DatoMeteorologicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SincronizacionClimaticaScheduler {

    private final ParcelaRepository parcelaRepository;
    private final ClimaExternoService climaExternoService;
    private final DatoMeteorologicoService datoMeteorologicoService;
    private final AlertaService alertaService;

    @Scheduled(cron = "${clima.sincronizacion.cron:0 0 */1 * * *}")
    public SincronizacionResultadoDto sincronizarYEvaluarParcelas() {
        log.info("Iniciando tarea programada: Sincronización climática y evaluación de riesgos para parcelas activas...");

        List<Parcela> parcelasActivas = parcelaRepository.findByActivaTrue();
        int totalProcesadas = 0;
        int sincronizadasExitosas = 0;
        int parcelasConError = 0;
        int totalAlertasGeneradas = 0;
        List<String> detalles = new ArrayList<>();

        for (Parcela parcela : parcelasActivas) {
            totalProcesadas++;
            Comunidad comunidad = parcela.getComunidad();

            if (comunidad == null || comunidad.getLatitud() == null || comunidad.getLongitud() == null) {
                log.warn("Parcela ID: {} '{}' no tiene comunidad o coordenadas asociadas. Omitiendo...",
                        parcela.getId(), parcela.getNombre());
                parcelasConError++;
                detalles.add("Parcela " + parcela.getId() + " omitida por falta de coordenadas comunitarias.");
                continue;
            }

            try {
                DatoMeteorologicoRequestDto datoClimatico = climaExternoService.consultarClimaActual(
                        comunidad.getLatitud(), comunidad.getLongitud());

                if (datoClimatico == null) {
                    log.warn("No se pudo obtener datos climáticos para Parcela ID: {}", parcela.getId());
                    parcelasConError++;
                    detalles.add("Parcela " + parcela.getId() + ": No se recibió respuesta climática externa.");
                    continue;
                }

                datoClimatico.setParcelaId(parcela.getId());
                datoMeteorologicoService.registrarDato(datoClimatico);

                List<AlertaResponseDto> alertas = alertaService.evaluarClimaYGenerarAlerta(parcela.getId());
                sincronizadasExitosas++;
                totalAlertasGeneradas += alertas.size();

                String detalle = String.format("Parcela ID: %d sincronizada exitosamente. Temp: %.1f°C. Alertas generadas: %d",
                        parcela.getId(), datoClimatico.getTemperatura(), alertas.size());
                detalles.add(detalle);
                log.info(detalle);

            } catch (Exception ex) {
                log.error("Error al sincronizar parcela ID {}: {}", parcela.getId(), ex.getMessage(), ex);
                parcelasConError++;
                detalles.add("Error en parcela " + parcela.getId() + ": " + ex.getMessage());
            }
        }

        SincronizacionResultadoDto resultado = SincronizacionResultadoDto.builder()
                .ejecutadoEn(LocalDateTime.now())
                .totalParcelasProcesadas(totalProcesadas)
                .parcelasSincronizadasExitosamente(sincronizadasExitosas)
                .parcelasConError(parcelasConError)
                .totalAlertasGeneradas(totalAlertasGeneradas)
                .detalles(detalles)
                .build();

        log.info("Tarea de sincronización finalizada: {} procesadas, {} exitosas, {} con error, {} alertas generadas.",
                totalProcesadas, sincronizadasExitosas, parcelasConError, totalAlertasGeneradas);

        return resultado;
    }
}
