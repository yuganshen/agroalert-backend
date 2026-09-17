// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Agricultor.java
package com.agroalert.agroalertbackend.entity;

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
@Table(name = "agricultor")
public class Agricultor extends Usuario {

    private String dni;

    private String telefono;
}
