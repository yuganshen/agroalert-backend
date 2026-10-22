// Ruta: src/main/java/com/agroalert/agroalertbackend/service/ReglaDecisionService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ReglaDecisionRequestDto;
import com.agroalert.agroalertbackend.dto.ReglaDecisionResponseDto;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.entity.ReglaDecision;
import com.agroalert.agroalertbackend.entity.Riesgo;
import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import com.agroalert.agroalertbackend.repository.ReglaDecisionRepository;
import com.agroalert.agroalertbackend.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReglaDecisionService {

    private final ReglaDecisionRepository reglaDecisionRepository;
    private final RiesgoRepository riesgoRepository;
    private final EtapaFenologicaRepository etapaFenologicaRepository;

    @Transactional
    public ReglaDecisionResponseDto crearRegla(ReglaDecisionRequestDto dto) {
        Riesgo riesgo = riesgoRepository.findById(dto.getRiesgoId())
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con ID: " + dto.getRiesgoId()));

        EtapaFenologica etapa = etapaFenologicaRepository.findById(dto.getEtapaFenologicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Etapa fenológica no encontrada con ID: " + dto.getEtapaFenologicaId()));

        ReglaDecision regla = ReglaDecision.builder()
                .variableMeteorologica(dto.getVariableMeteorologica())
                .operador(dto.getOperador())
                .umbral(dto.getUmbral())
                .fuenteTecnica(dto.getFuenteTecnica())
                .nivelResultante(dto.getNivelResultante())
                .estado(EstadoValidacion.VALIDADO)
                .riesgo(riesgo)
                .etapaFenologica(etapa)
                .build();

        ReglaDecision guardada = reglaDecisionRepository.save(regla);
        return mapToResponseDto(guardada);
    }

    @Transactional(readOnly = true)
    public List<ReglaDecisionResponseDto> listarReglas() {
        return reglaDecisionRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReglaDecisionResponseDto obtenerReglaPorId(Long id) {
        ReglaDecision regla = reglaDecisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de decisión no encontrada con ID: " + id));
        return mapToResponseDto(regla);
    }

    @Transactional
    public ReglaDecisionResponseDto actualizarRegla(Long id, ReglaDecisionRequestDto dto) {
        ReglaDecision regla = reglaDecisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de decisión no encontrada con ID: " + id));

        Riesgo riesgo = riesgoRepository.findById(dto.getRiesgoId())
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con ID: " + dto.getRiesgoId()));

        EtapaFenologica etapa = etapaFenologicaRepository.findById(dto.getEtapaFenologicaId())
                .orElseThrow(() -> new ResourceNotFoundException("Etapa fenológica no encontrada con ID: " + dto.getEtapaFenologicaId()));

        regla.setVariableMeteorologica(dto.getVariableMeteorologica());
        regla.setOperador(dto.getOperador());
        regla.setUmbral(dto.getUmbral());
        regla.setFuenteTecnica(dto.getFuenteTecnica());
        regla.setNivelResultante(dto.getNivelResultante());
        regla.setRiesgo(riesgo);
        regla.setEtapaFenologica(etapa);

        ReglaDecision actualizada = reglaDecisionRepository.save(regla);
        return mapToResponseDto(actualizada);
    }

    @Transactional
    public void eliminarRegla(Long id) {
        if (!reglaDecisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Regla de decisión no encontrada con ID: " + id);
        }
        reglaDecisionRepository.deleteById(id);
    }

    private ReglaDecisionResponseDto mapToResponseDto(ReglaDecision regla) {
        String riesgoNombre = regla.getRiesgo() != null ? regla.getRiesgo().getNombre() : null;
        String etapaNombre = regla.getEtapaFenologica() != null ? regla.getEtapaFenologica().getNombre() : null;

        return ReglaDecisionResponseDto.builder()
                .id(regla.getId())
                .variableMeteorologica(regla.getVariableMeteorologica())
                .operador(regla.getOperador())
                .umbral(regla.getUmbral())
                .fuenteTecnica(regla.getFuenteTecnica())
                .nivelResultante(regla.getNivelResultante())
                .riesgoNombre(riesgoNombre)
                .etapaFenologicaNombre(etapaNombre)
                .build();
    }
}
