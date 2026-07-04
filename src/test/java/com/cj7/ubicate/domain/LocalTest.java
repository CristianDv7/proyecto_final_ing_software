package com.cj7.ubicate.domain;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.EstadoPublicacion;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalTest {

    private Ubicacion ubicacion() {
        return new Ubicacion(-0.18, -78.46, OrigenUbicacion.GPS, null);
    }

    private List<HorarioAtencion> horarios() {
        return List.of(new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false));
    }

    @Test
    void publicaLocalConDatosObligatorios() {
        UUID comercianteId = UUID.randomUUID();
        Local local = Local.publicar("Panadería", 3L, ubicacion(), "+593987654321",
                horarios(), null, comercianteId);

        assertNotNull(local.getId());
        assertEquals(EstadoPublicacion.PUBLICADO, local.getEstadoPublicacion());
        assertTrue(local.getServicios().isEmpty());
        assertEquals(comercianteId, local.getComercianteId());
    }

    @Test
    void informaTodosLosCamposObligatoriosFaltantes() {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> Local.publicar("  ", null, null, "", List.of(), null, UUID.randomUUID()));
        assertTrue(ex.getCampos().containsAll(
                List.of("nombre", "tipoNegocioId", "ubicacion", "whatsapp", "horarios")));
    }

    @Test
    void reconstruyeLocalDesdePersistencia() {
        UUID id = UUID.randomUUID();
        Local local = Local.reconstruir(id, "Local", 1L, ubicacion(), "+593900000000",
                horarios(), List.of("Domicilio"), EstadoPublicacion.PUBLICADO, UUID.randomUUID(),
                java.time.Instant.now());
        assertEquals(id, local.getId());
        assertEquals(List.of("Domicilio"), local.getServicios());
    }
}
