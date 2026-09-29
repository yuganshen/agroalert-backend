// Ruta: src/main/java/com/agroalert/agroalertbackend/exception/ResourceNotFoundException.java
package com.agroalert.agroalertbackend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
