// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/EtapaFenologicaDto.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtapaFenologicaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer diasDesdeMin;
    private Integer diasDesdeMax;
    private String fuenteTecnica;
}
