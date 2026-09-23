// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Alerta.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NivelRiesgo nivel;

    @Column(name = "generada_en")
    private LocalDateTime generadaEn;

    @Enumerated(EnumType.STRING)
    private EstadoAlerta estado;

    @Column(name = "condicion_detectada")
    private String condicionDetectada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcela_id")
    private Parcela parcela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riesgo_id")
    private Riesgo riesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regla_decision_id")
    private ReglaDecision reglaDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medida_id")
    private Medida medida;
}
