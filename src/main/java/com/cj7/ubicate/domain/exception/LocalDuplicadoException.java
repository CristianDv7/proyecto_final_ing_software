package com.cj7.ubicate.domain.exception;

/**
 * Se lanza cuando ya existe un local con el mismo WhatsApp y una ubicación
 * equivalente (HTTP 409).
 */
public class LocalDuplicadoException extends RuntimeException {

    public LocalDuplicadoException(String message) {
        super(message);
    }
}
