package com.cj7.ubicate.integration;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.application.port.in.RegistrarLocalUseCasePort;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import com.cj7.ubicate.infrastructure.persistence.ComercianteJpaRepository;
import com.cj7.ubicate.infrastructure.persistence.LocalJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * FR-012 (concurrencia): ante solicitudes concurrentes sobre la misma identidad y
 * ubicación, solo una debe prosperar (sin doble reserva por condición de carrera).
 * La garantía es la restricción única (whatsapp + ubicación) a nivel de base de datos.
 */
@SpringBootTest
class RegistroLocalConcurrenciaTest {

    @Autowired
    private RegistrarLocalUseCasePort registrarLocal;

    @Autowired
    private LocalJpaRepository localRepository;

    @Autowired
    private ComercianteJpaRepository comercianteRepository;

    @BeforeEach
    void limpiar() {
        localRepository.deleteAll();
        comercianteRepository.deleteAll();
    }

    private NuevoLocal comando() {
        return new NuevoLocal("Panadería La Espiga", 3L,
                new Ubicacion(-0.180653, -78.467834, OrigenUbicacion.GPS, null),
                "+593987651234",
                List.of(new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false)),
                List.of());
    }

    @Test
    void anteSolicitudesConcurrentesSobreLaMismaFranjaSoloUnaProspera() throws Exception {
        int hilos = 8;
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        CountDownLatch listos = new CountDownLatch(hilos);
        CountDownLatch salida = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();

        for (int i = 0; i < hilos; i++) {
            pool.submit(() -> {
                listos.countDown();
                try {
                    salida.await();
                    registrarLocal.registrar(comando());
                    exitos.incrementAndGet();
                } catch (RuntimeException ignorado) {
                    // La solicitud que pierde la carrera se rechaza (duplicado / restricción única).
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        listos.await();               // todos los hilos preparados
        salida.countDown();           // arrancan simultáneamente
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        assertEquals(1, exitos.get(), "solo una solicitud concurrente debe prosperar");
        assertEquals(1, localRepository.count(), "no debe haber doble reserva del local");
        assertEquals(1, comercianteRepository.count(), "el comerciante se crea una sola vez");
    }
}
