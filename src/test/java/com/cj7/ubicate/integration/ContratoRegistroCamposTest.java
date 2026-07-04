package com.cj7.ubicate.integration;

import com.cj7.ubicate.infrastructure.web.generated.model.RegistrarLocalRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FR-001 (invariante verificable en backend): el registro cabe en una sola pantalla
 * porque el contrato de registro es una única operación que expone COMO MÁXIMO 10
 * campos. La presentación efectiva en una sola pantalla es responsabilidad del cliente
 * móvil (ver Clarifications del spec); aquí se verifica el límite de campos del contrato.
 */
class ContratoRegistroCamposTest {

    @Test
    void elContratoDeRegistroNoExcedeDiezCampos() {
        long campos = Arrays.stream(RegistrarLocalRequest.class.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .count();

        assertTrue(campos <= 10,
                "el contrato de registro debe exponer <= 10 campos (una sola pantalla); tiene " + campos);
    }
}
