// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Riesgo.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.EstadoValidacion;
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
@Table(name = "riesgo")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    @Column(name = "variables_asociadas")
    private String variablesAsociadas;

    @Enumerated(EnumType.STRING)
    private EstadoValidacion estado;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}
