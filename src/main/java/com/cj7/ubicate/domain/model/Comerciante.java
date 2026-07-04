package com.cj7.ubicate.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Comerciante propietario del local. Se identifica por su número de WhatsApp.
 */
public record Comerciante(UUID id, String whatsapp, Instant fechaRegistro) {

    /** Crea un comerciante nuevo (identidad recién generada) a partir del WhatsApp. */
    public static Comerciante nuevo(String whatsapp) {
        return new Comerciante(UUID.randomUUID(), whatsapp, Instant.now());
    }
}
