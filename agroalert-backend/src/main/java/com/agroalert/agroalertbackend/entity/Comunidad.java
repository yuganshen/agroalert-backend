// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Comunidad.java
package com.agroalert.agroalertbackend.entity;

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
@Table(name = "comunidad")
public class Comunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String distrito;

    private String provincia;

    private Double latitud;

    private Double longitud;

    @Builder.Default
    private Boolean activa = true;
}
