// Ruta: src/main/java/com/agroalert/agroalertbackend/service/DatoMeteorologicoService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.DatoMeteorologicoRequestDto;
import com.agroalert.agroalertbackend.dto.DatoMeteorologicoResponseDto;
import com.agroalert.agroalertbackend.entity.DatoMeteorologico;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.entity.enums.TipoDato;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.DatoMeteorologicoRepository;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatoMeteorologicoService {

    private final DatoMeteorologicoRepository datoMeteorologicoRepository;
    private final ParcelaRepository parcelaRepository;

    @Transactional
    public DatoMeteorologicoResponseDto registrarDato(DatoMeteorologicoRequestDto dto) {
        Parcela parcela = parcelaRepository.findById(dto.getParcelaId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada con ID: " + dto.getParcelaId()));

        LocalDateTime fechaHora = dto.getFechaHora() != null ? dto.getFechaHora() : LocalDateTime.now();
        TipoDato tipoDato = dto.getTipoDato() != null ? dto.getTipoDato() : TipoDato.ACTUAL;

        DatoMeteorologico datoMeteorologico = DatoMeteorologico.builder()
                .parcela(parcela)
                .fechaHora(fechaHora)
                .temperatura(dto.getTemperatura())
                .precipitacion(dto.getPrecipitacion())
                .humedadRelativa(dto.getHumedadRelativa())
                .velocidadViento(dto.getVelocidadViento())
                .tipoDato(tipoDato)
                .fuente(dto.getFuente())
                .build();

        DatoMeteorologico guardado = datoMeteorologicoRepository.save(datoMeteorologico);
        return mapToResponseDto(guardado);
    }

    @Transactional(readOnly = true)
    public List<DatoMeteorologicoResponseDto> obtenerHistorialPorParcela(Long parcelaId) {
        if (!parcelaRepository.existsById(parcelaId)) {
            throw new ResourceNotFoundException("Parcela no encontrada con ID: " + parcelaId);
        }

        return datoMeteorologicoRepository.findByParcelaIdOrderByFechaHoraDesc(parcelaId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DatoMeteorologicoResponseDto obtenerUltimoDatoPorParcela(Long parcelaId) {
        if (!parcelaRepository.existsById(parcelaId)) {
            throw new ResourceNotFoundException("Parcela no encontrada con ID: " + parcelaId);
        }

        DatoMeteorologico dato = datoMeteorologicoRepository.findTopByParcelaIdOrderByFechaHoraDesc(parcelaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron datos meteorológicos para la parcela con ID: " + parcelaId));

        return mapToResponseDto(dato);
    }

    private DatoMeteorologicoResponseDto mapToResponseDto(DatoMeteorologico dato) {
        Long parcelaId = dato.getParcela() != null ? dato.getParcela().getId() : null;
        String parcelaNombre = dato.getParcela() != null ? dato.getParcela().getNombre() : null;

        return DatoMeteorologicoResponseDto.builder()
                .id(dato.getId())
                .parcelaId(parcelaId)
                .parcelaNombre(parcelaNombre)
                .fechaHora(dato.getFechaHora())
                .temperatura(dato.getTemperatura())
                .precipitacion(dato.getPrecipitacion())
                .humedadRelativa(dato.getHumedadRelativa())
                .velocidadViento(dato.getVelocidadViento())
                .tipoDato(dato.getTipoDato())
                .fuente(dato.getFuente())
                .build();
    }
}
