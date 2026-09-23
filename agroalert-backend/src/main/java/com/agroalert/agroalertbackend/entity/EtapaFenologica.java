// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/EtapaFenologica.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "etapa_fenologica")
public class EtapaFenologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    @Column(name = "dias_desde_min")
    private Integer diasDesdeMin;

    @Column(name = "dias_desde_max")
    private Integer diasDesdeMax;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @Column(name = "fuente_tecnica")
    private String fuenteTecnica;
}
