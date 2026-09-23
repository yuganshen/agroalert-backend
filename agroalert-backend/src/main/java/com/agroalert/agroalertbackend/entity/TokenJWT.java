// Ruta: src/main/java/com/agroalert/agroalertbackend/entity/TokenJWT.java
package com.agroalert.agroalertbackend.entity;

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
@Table(name = "token_jwt")
public class TokenJWT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(name = "emitido_en")
    private LocalDateTime emitidoEn;

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @Builder.Default
    private Boolean revocado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
