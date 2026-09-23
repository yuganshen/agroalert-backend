// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Parcela.java
package com.agroalert.agroalertbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parcela")
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String ubicacion;

    @Column(name = "area_hectareas")
    private Double areaHectareas;

    @Column(name = "fecha_siembra")
    private LocalDate fechaSiembra;

    @Builder.Default
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agricultor_id")
    private Agricultor agricultor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunidad_id")
    private Comunidad comunidad;
}
