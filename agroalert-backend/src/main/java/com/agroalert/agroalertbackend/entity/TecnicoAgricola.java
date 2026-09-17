// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/TecnicoAgricola.java
package com.agroalert.agroalertbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tecnico_agricola")
public class TecnicoAgricola extends Usuario {

    @Column(name = "codigo_profesional")
    private String codigoProfesional;

    private String especialidad;
}
