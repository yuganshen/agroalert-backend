// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/DatoMeteorologico.java
package com.agroalert.agroalertbackend.entity;

import com.agroalert.agroalertbackend.entity.enums.TipoDato;
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
@Table(name = "dato_meteorologico")
public class DatoMeteorologico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private Double temperatura;

    private Double precipitacion;

    @Column(name = "humedad_relativa")
    private Double humedadRelativa;

    @Column(name = "velocidad_viento")
    private Double velocidadViento;

    private String fuente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato")
    private TipoDato tipoDato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcela_id")
    private Parcela parcela;
}
