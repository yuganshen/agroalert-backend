// Ruta: src/main/java/com/agroalert/agroalertbackend/exception/UnauthorizedException.java
package com.agroalert.agroalertbackend.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
