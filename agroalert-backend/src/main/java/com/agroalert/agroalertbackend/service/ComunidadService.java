// Ruta: src/main/java/com/agroalert/agroalertbackend/service/ComunidadService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.dto.ComunidadDto;
import com.agroalert.agroalertbackend.dto.ComunidadResponseDto;
import com.agroalert.agroalertbackend.entity.Comunidad;
import com.agroalert.agroalertbackend.exception.ResourceNotFoundException;
import com.agroalert.agroalertbackend.repository.ComunidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComunidadService {

    private final ComunidadRepository comunidadRepository;

    @Transactional
    public ComunidadResponseDto crearComunidad(ComunidadDto dto) {
        Comunidad comunidad = Comunidad.builder()
                .nombre(dto.getNombre())
                .distrito(dto.getDistrito())
                .provincia(dto.getProvincia())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .activa(true)
                .build();

        Comunidad comunidadGuardada = comunidadRepository.save(comunidad);
        return mapToResponseDto(comunidadGuardada);
    }

    @Transactional(readOnly = true)
    public List<ComunidadResponseDto> listarComunidades() {
        return comunidadRepository.findByActivaTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComunidadResponseDto actualizarComunidad(Long id, ComunidadDto dto) {
        Comunidad comunidad = comunidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunidad no encontrada con ID: " + id));

        comunidad.setNombre(dto.getNombre());
        comunidad.setDistrito(dto.getDistrito());
        comunidad.setProvincia(dto.getProvincia());
        comunidad.setLatitud(dto.getLatitud());
        comunidad.setLongitud(dto.getLongitud());

        Comunidad comunidadActualizada = comunidadRepository.save(comunidad);
        return mapToResponseDto(comunidadActualizada);
    }

    @Transactional
    public void eliminarComunidad(Long id) {
        Comunidad comunidad = comunidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunidad no encontrada con ID: " + id));

        comunidad.setActiva(false);
        comunidadRepository.save(comunidad);
    }

    private ComunidadResponseDto mapToResponseDto(Comunidad comunidad) {
        return ComunidadResponseDto.builder()
                .id(comunidad.getId())
                .nombre(comunidad.getNombre())
                .distrito(comunidad.getDistrito())
                .provincia(comunidad.getProvincia())
                .latitud(comunidad.getLatitud())
                .longitud(comunidad.getLongitud())
                .activa(comunidad.getActiva())
                .build();
    }
}
