// Ruta: src/main/java/com/agroalert/agroalertbackend/service/AlertaService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.AlertaResponseDto;
import com.agroalert.agroalertbackend.entity.*;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final ParcelaRepository parcelaRepository;
    private final DatoMeteorologicoRepository datoMeteorologicoRepository;
    private final ReglaDecisionRepository reglaDecisionRepository;
    private final EtapaFenologicaRepository etapaFenologicaRepository;
    private final MedidaRepository medidaRepository;
    private final RecomendacionRepository recomendacionRepository;
    private final ProcedimientoRepository procedimientoRepository;

    @Transactional
    public List<AlertaResponseDto> evaluarClimaYGenerarAlerta(Long parcelaId) {
        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada con ID: " + parcelaId));

        DatoMeteorologico ultimoDato = datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron datos meteorológicos para evaluar la parcela con ID: " + parcelaId));

        List<ReglaDecision> reglasVigentes = new ArrayList<>();
        if (parcela.getFechaSiembra() != null) {
            long dias = ChronoUnit.DAYS.between(parcela.getFechaSiembra(), LocalDate.now());
            if (dias >= 0) {
                etapaFenologicaRepository.findEtapaPorDias((int) dias)
                        .ifPresent(etapa -> reglasVigentes.addAll(reglaDecisionRepository.findByEtapaFenologicaId(etapa.getId())));
            }
        }

        List<Alerta> alertasGeneradas = new ArrayList<>();
        for (ReglaDecision regla : reglasVigentes) {
            Double valorActual = obtenerValorVariable(ultimoDato, regla.getVariableMeteorologica());
            if (evaluarCondicion(valorActual, regla.getOperador(), regla.getUmbral())) {
                Medida medidaPreventiva = null;
                if (regla.getRiesgo() != null) {
                    List<Medida> medidas = medidaRepository.findByRiesgoId(regla.getRiesgo().getId());
                    if (!medidas.isEmpty()) {
                        medidaPreventiva = medidas.get(0);
                    }
                }

                String condicionDetectada = String.format("%s (%.2f) %s %.2f",
                        regla.getVariableMeteorologica(),
                        valorActual,
                        regla.getOperador(),
                        regla.getUmbral());

                NivelRiesgo nivel = regla.getNivelResultante() != null ? regla.getNivelResultante() : NivelRiesgo.MODERADO;

                Alerta nuevaAlerta = Alerta.builder()
                        .parcela(parcela)
                        .riesgo(regla.getRiesgo())
                        .reglaDecision(regla)
                        .medida(medidaPreventiva)
                        .nivel(nivel)
                        .estado(EstadoAlerta.ACTIVA)
                        .generadaEn(LocalDateTime.now())
                        .condicionDetectada(condicionDetectada)
                        .build();

                alertasGeneradas.add(alertaRepository.save(nuevaAlerta));
            }
        }

        return alertasGeneradas.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertaResponseDto> listarAlertasPorParcela(Long parcelaId) {
        if (!parcelaRepository.existsById(parcelaId)) {
            throw new ResourceNotFoundException("Parcela no encontrada con ID: " + parcelaId);
        }

        return alertaRepository.findByParcelaIdOrderByGeneradaEnDesc(parcelaId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertaResponseDto cambiarEstadoAlerta(Long alertaId, EstadoAlerta nuevoEstado) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + alertaId));

        alerta.setEstado(nuevoEstado);
        Alerta guardada = alertaRepository.save(alerta);
        return mapToResponseDto(guardada);
    }

    private Double obtenerValorVariable(DatoMeteorologico dato, String variable) {
        if (variable == null) return null;
        String normalizada = variable.toLowerCase().trim()
                .replace(" ", "")
                .replace("_", "");

        return switch (normalizada) {
            case "temperatura" -> dato.getTemperatura();
            case "precipitacion" -> dato.getPrecipitacion();
            case "humedadrelativa", "humedad" -> dato.getHumedadRelativa();
            case "velocidadviento", "viento" -> dato.getVelocidadViento();
            default -> null;
        };
    }

    private boolean evaluarCondicion(Double valor, String operador, Double umbral) {
        if (valor == null || operador == null || umbral == null) {
            return false;
        }
        return switch (operador.trim()) {
            case ">" -> valor > umbral;
            case "<" -> valor < umbral;
            case ">=" -> valor >= umbral;
            case "<=" -> valor <= umbral;
            case "==", "=" -> Math.abs(valor - umbral) < 0.0001;
            case "!=" -> Math.abs(valor - umbral) >= 0.0001;
            default -> false;
        };
    }

    private AlertaResponseDto mapToResponseDto(Alerta alerta) {
        Long parcelaId = alerta.getParcela() != null ? alerta.getParcela().getId() : null;
        String parcelaNombre = alerta.getParcela() != null ? alerta.getParcela().getNombre() : null;
        String riesgoNombre = alerta.getRiesgo() != null ? alerta.getRiesgo().getNombre() : null;
        String medidaNombre = alerta.getMedida() != null ? alerta.getMedida().getNombre() : null;

        String recomendacionTexto = null;
        String procedimientoTitulo = null;

        if (alerta.getMedida() != null) {
            recomendacionTexto = recomendacionRepository.findByMedidaId(alerta.getMedida().getId())
                    .map(Recomendacion::getTexto)
                    .orElse(null);

            procedimientoTitulo = procedimientoRepository.findByMedidaId(alerta.getMedida().getId())
                    .map(Procedimiento::getTitulo)
                    .orElse(null);
        }

        return AlertaResponseDto.builder()
                .id(alerta.getId())
                .generadaEn(alerta.getGeneradaEn())
                .condicionDetectada(alerta.getCondicionDetectada())
                .nivel(alerta.getNivel())
                .estado(alerta.getEstado())
                .parcelaId(parcelaId)
                .parcelaNombre(parcelaNombre)
                .riesgoNombre(riesgoNombre)
                .medidaNombre(medidaNombre)
                .recomendacionTexto(recomendacionTexto)
                .procedimientoTitulo(procedimientoTitulo)
                .build();
    }
}
