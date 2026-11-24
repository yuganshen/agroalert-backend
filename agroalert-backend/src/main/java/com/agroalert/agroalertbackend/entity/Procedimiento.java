// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Procedimiento.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "procedimiento")
public class Procedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean activo = true;

    private String comentarioValidacion;

    private String titulo;

    @Column(name = "momento_aplicacion")
    private String momentoAplicacion;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medida_id")
    private Medida medida;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "procedimiento", fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<PasoProcedimiento> pasos = new ArrayList<>();
}
