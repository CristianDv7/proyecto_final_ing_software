package com.cj7.ubicate.integration;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.application.port.in.RegistrarLocalUseCasePort;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import com.cj7.ubicate.domain.port.LocalRepositoryPort;
import com.cj7.ubicate.infrastructure.persistence.ComercianteJpaRepository;
import com.cj7.ubicate.infrastructure.persistence.LocalJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FR-012 (atomicidad): un fallo o interrupción al guardar NO debe dejar un perfil
 * parcialmente publicado. Se fuerza un fallo al persistir el local (con el comerciante
 * ya creado en la misma transacción) y se verifica que el rollback revierte TODO.
 */
@SpringBootTest
class RegistroLocalAtomicidadTest {

    /** Sustituye el puerto de locales por uno que falla al persistir, dentro de la transacción del caso de uso. */
    @TestConfiguration
    static class FalloAlPersistirLocalConfig {
        @Bean
        @Primary
        LocalRepositoryPort localRepositoryPortQueFalla() {
            return new LocalRepositoryPort() {
                @Override
                public Local guardar(Local local) {
                    throw new RuntimeException("fallo simulado al persistir el local");
                }

                @Override
                public boolean existeDuplicado(String whatsapp, double latitud, double longitud) {
                    return false;
                }
            };
        }
    }

    @Autowired
    private RegistrarLocalUseCasePort registrarLocal;

    @Autowired
    private ComercianteJpaRepository comercianteRepository;

    @Autowired
    private LocalJpaRepository localRepository;

    @BeforeEach
    void limpiar() {
        localRepository.deleteAll();
        comercianteRepository.deleteAll();
    }

    @Test
    void unFalloAlGuardarElLocalRevierteTambienElComercianteRecienCreado() {
        NuevoLocal comando = new NuevoLocal("Panadería La Espiga", 3L,
                new Ubicacion(-0.180653, -78.467834, OrigenUbicacion.GPS, null),
                "+593987659000",
                List.of(new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false)),
                List.of());

        assertThrows(RuntimeException.class, () -> registrarLocal.registrar(comando));

        // Atomicidad: el registro es todo-o-nada. Al fallar el guardado del local, la
        // transacción @Transactional revierte el comerciante insertado momentos antes:
        // no queda ningún estado parcialmente publicado.
        assertEquals(0, comercianteRepository.count(), "el comerciante creado debe revertirse (rollback)");
        assertEquals(0, localRepository.count(), "no debe quedar ningún local persistido");
    }
}
