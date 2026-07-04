package com.cj7.ubicate.domain;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UbicacionTest {

    @Test
    void creaUbicacionValida() {
        Ubicacion u = new Ubicacion(-0.18, -78.46, OrigenUbicacion.GPS, "Av. Amazonas");
        assertEquals(OrigenUbicacion.GPS, u.origen());
        assertEquals(-0.18, u.latitud());
    }

    @Test
    void rechazaLatitudFueraDeRango() {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> new Ubicacion(95.0, 0.0, OrigenUbicacion.GPS, null));
        assertEquals(java.util.List.of("ubicacion.latitud"), ex.getCampos());
    }

    @Test
    void rechazaLongitudFueraDeRango() {
        assertThrows(DatosInvalidosException.class,
                () -> new Ubicacion(0.0, 200.0, OrigenUbicacion.MANUAL, null));
    }

    @Test
    void rechazaOrigenNulo() {
        assertThrows(DatosInvalidosException.class,
                () -> new Ubicacion(0.0, 0.0, null, null));
    }
}
