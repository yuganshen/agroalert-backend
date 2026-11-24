// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Medida.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "medida")
public class Medida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean activo = true;

    private String comentarioValidacion;

    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riesgo_id")
    private Riesgo riesgo;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "medida", fetch = FetchType.LAZY)
    private Recomendacion recomendacion;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "medida", fetch = FetchType.LAZY)
    private Procedimiento procedimiento;
}
