// Ruta: src/main/java/com/agroalert/agroalertbackend/exception/ValidationException.java
package com.agroalert.agroalertbackend.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
