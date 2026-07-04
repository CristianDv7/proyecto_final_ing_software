package com.cj7.ubicate.domain;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorarioAtencionTest {

    @Test
    void creaHorarioValido() {
        HorarioAtencion h = new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false);
        assertEquals(DiaSemana.LUN, h.diaSemana());
    }

    @Test
    void permiteCruceDeMedianoche() {
        HorarioAtencion h = new HorarioAtencion(DiaSemana.VIE, LocalTime.of(22, 0), LocalTime.of(2, 0), true);
        assertTrue(h.cruzaMedianoche());
    }

    @Test
    void rechazaCierreAntesDeAperturaSinCruce() {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> new HorarioAtencion(DiaSemana.LUN, LocalTime.of(18, 0), LocalTime.of(8, 0), false));
        assertEquals(java.util.List.of("horarios"), ex.getCampos());
    }

    @Test
    void rechazaCamposNulos() {
        assertThrows(DatosInvalidosException.class,
                () -> new HorarioAtencion(null, null, null, false));
    }
}
