// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ParcelaRequestDto.java
package com.agroalert.agroalertbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelaRequestDto {

    @NotBlank(message = "El nombre de la parcela es obligatorio")
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "El área en hectáreas es obligatoria")
    @Positive(message = "El área en hectáreas debe ser un número positivo mayor a cero")
    private Double areaHectareas;

    @NotNull(message = "La fecha de siembra es obligatoria")
    private LocalDate fechaSiembra;

    @NotNull(message = "El ID de la comunidad es obligatorio")
    private Long comunidadId;

    @NotNull(message = "El ID del agricultor es obligatorio")
    private Long agricultorId;
}
