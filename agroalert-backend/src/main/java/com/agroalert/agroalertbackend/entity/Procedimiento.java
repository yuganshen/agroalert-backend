// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Procedimiento.java
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
@Table(name = "procedimiento")
public class Procedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(name = "momento_aplicacion")
    private String momentoAplicacion;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medida_id")
    private Medida medida;
}
