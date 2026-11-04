// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/ContenidoDto.java
package com.agroalert.agroalertbackend.dto;
import com.agroalert.agroalertbackend.entity.enums.*;
import lombok.Data;
import java.util.List;
@Data
public class ContenidoDto {
    private Long id;
    private String nombre, descripcion, fuenteTecnica, variablesAsociadas, variableMeteorologica, operador, texto, titulo, momentoAplicacion, comentarioValidacion;
    private Integer diasDesdeMin, diasDesdeMax;
    private Double umbral;
    private Long riesgoId, etapaFenologicaId, medidaId;
    private EstadoValidacion estado;
    private NivelRiesgo nivelResultante;
    private Boolean activo;
    private List<String> pasos;
}
