// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/ReglaDecision.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
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
@Table(name = "regla_decision")
public class ReglaDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variable_meteorologica")
    private String variableMeteorologica;

    private String operador;

    private Double umbral;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_resultante")
    private NivelRiesgo nivelResultante;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @Column(name = "fuente_tecnica")
    private String fuenteTecnica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riesgo_id")
    private Riesgo riesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_fenologica_id")
    private EtapaFenologica etapaFenologica;
}
