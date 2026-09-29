// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/AuthResponse.java
package com.agroalert.agroalertbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String email;

    private List<String> roles;
}
