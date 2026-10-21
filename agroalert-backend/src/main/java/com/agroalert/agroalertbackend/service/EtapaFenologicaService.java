// Ruta: src/main/java/com/agroalert/agroalertbackend/service/EtapaFenologicaService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.EtapaFenologicaDto;
import com.agroalert.agroalertbackend.entity.EtapaFenologica;
import com.agroalert.agroalertbackend.repository.EtapaFenologicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtapaFenologicaService {

    private final EtapaFenologicaRepository etapaFenologicaRepository;

    @Transactional(readOnly = true)
    public List<EtapaFenologicaDto> listarEtapas() {
        return etapaFenologicaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private EtapaFenologicaDto mapToDto(EtapaFenologica etapa) {
        return EtapaFenologicaDto.builder()
                .id(etapa.getId())
                .nombre(etapa.getNombre())
                .descripcion(etapa.getDescripcion())
                .diasDesdeMin(etapa.getDiasDesdeMin())
                .diasDesdeMax(etapa.getDiasDesdeMax())
                .fuenteTecnica(etapa.getFuenteTecnica())
                .build();
    }
}
