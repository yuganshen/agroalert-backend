// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/PasoProcedimiento.java
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
@Table(name = "paso_procedimiento")
public class PasoProcedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orden;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedimiento_id")
    private Procedimiento procedimiento;
}
