// Ruta: src/main/java/com/agroalert/agroalertbackend/service/ParcelaService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ParcelaRequestDto;
import com.agroalert.agroalertbackend.dto.ParcelaResponseDto;
import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.entity.Parcela;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.AgricultorRepository;
import com.agroalert.agroalertbackend.repository.ComunidadRepository;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import com.agroalert.agroalertbackend.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;
    private final AgricultorRepository agricultorRepository;
    private final ComunidadRepository comunidadRepository;
    private final EtapaFenologicaRepository etapaFenologicaRepository;

    @Transactional
    public ParcelaResponseDto registrarParcela(ParcelaRequestDto dto) {
        Agricultor agricultor = agricultorRepository.findById(dto.getAgricultorId())
                .orElseThrow(() -> new ResourceNotFoundException("Agricultor no encontrado con ID: " + dto.getAgricultorId()));

        Comunidad comunidad = comunidadRepository.findById(dto.getComunidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Comunidad no encontrada con ID: " + dto.getComunidadId()));

        Parcela parcela = Parcela.builder()
                .nombre(dto.getNombre())
                .ubicacion(dto.getUbicacion())
                .areaHectareas(dto.getAreaHectareas())
                .fechaSiembra(dto.getFechaSiembra())
                .activa(true)
                .agricultor(agricultor)
                .comunidad(comunidad)
                .build();

        Parcela parcelaGuardada = parcelaRepository.save(parcela);
        return mapToResponseDto(parcelaGuardada);
    }

    @Transactional(readOnly = true)
    public List<ParcelaResponseDto> listarParcelasPorAgricultor(Long agricultorId) {
        return parcelaRepository.findByAgricultorIdAndActivaTrue(agricultorId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParcelaResponseDto> listarParcelasPorComunidad(Long comunidadId) {
        return parcelaRepository.findByComunidadIdAndActivaTrue(comunidadId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParcelaResponseDto actualizarParcela(Long id, ParcelaRequestDto dto) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada con ID: " + id));

        Agricultor agricultor = agricultorRepository.findById(dto.getAgricultorId())
                .orElseThrow(() -> new ResourceNotFoundException("Agricultor no encontrado con ID: " + dto.getAgricultorId()));

        Comunidad comunidad = comunidadRepository.findById(dto.getComunidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Comunidad no encontrada con ID: " + dto.getComunidadId()));

        parcela.setNombre(dto.getNombre());
        parcela.setUbicacion(dto.getUbicacion());
        parcela.setAreaHectareas(dto.getAreaHectareas());
        parcela.setFechaSiembra(dto.getFechaSiembra());
        parcela.setAgricultor(agricultor);
        parcela.setComunidad(comunidad);

        Parcela parcelaActualizada = parcelaRepository.save(parcela);
        return mapToResponseDto(parcelaActualizada);
    }

    @Transactional
    public void desactivarParcela(Long id) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela no encontrada con ID: " + id));

        parcela.setActiva(false);
        parcelaRepository.save(parcela);
    }

    private String determinarEtapaFenologica(LocalDate fechaSiembra) {
        if (fechaSiembra == null) {
            return null;
        }
        long dias = ChronoUnit.DAYS.between(fechaSiembra, LocalDate.now());
        if (dias < 0) {
            return "Por iniciar";
        }
        return etapaFenologicaRepository.findEtapaPorDias((int) dias)
                .map(EtapaFenologica::getNombre)
                .orElse("No determinada");
    }

    private ParcelaResponseDto mapToResponseDto(Parcela parcela) {
        String comunidadNombre = parcela.getComunidad() != null ? parcela.getComunidad().getNombre() : null;
        String agricultorNombre = parcela.getAgricultor() != null
                ? (parcela.getAgricultor().getNombre() + " " + parcela.getAgricultor().getApellido()).trim()
                : null;
        String etapaFenologicaNombre = determinarEtapaFenologica(parcela.getFechaSiembra());

        return ParcelaResponseDto.builder()
                .id(parcela.getId())
                .nombre(parcela.getNombre())
                .ubicacion(parcela.getUbicacion())
                .areaHectareas(parcela.getAreaHectareas())
                .fechaSiembra(parcela.getFechaSiembra())
                .activa(parcela.getActiva())
                .comunidadNombre(comunidadNombre)
                .agricultorNombre(agricultorNombre)
                .etapaFenologicaNombre(etapaFenologicaNombre)
                .build();
    }
}
