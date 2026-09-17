// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/Administrador.java
package com.agroalert.agroalertbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "administrador")
public class Administrador extends Usuario {
}
