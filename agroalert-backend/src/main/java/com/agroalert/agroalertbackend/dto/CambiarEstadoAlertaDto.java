// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/CambiarEstadoAlertaDto.java
package com.agroalert.agroalertbackend.dto;

import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoAlertaDto {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoAlerta estado;
}
