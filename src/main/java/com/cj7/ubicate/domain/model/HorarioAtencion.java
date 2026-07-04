package com.cj7.ubicate.domain.model;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Franja de atención del local (value object). Valida presencia y coherencia
 * de apertura/cierre (salvo cruce de medianoche declarado).
 */
public record HorarioAtencion(DiaSemana diaSemana, LocalTime horaApertura, LocalTime horaCierre,
                              boolean cruzaMedianoche) {

    public HorarioAtencion {
        List<String> faltantes = new ArrayList<>();
        if (diaSemana == null) {
            faltantes.add("horarios.diaSemana");
        }
        if (horaApertura == null) {
            faltantes.add("horarios.horaApertura");
        }
        if (horaCierre == null) {
            faltantes.add("horarios.horaCierre");
        }
        if (!faltantes.isEmpty()) {
            throw new DatosInvalidosException(faltantes);
        }
        if (!cruzaMedianoche && !horaCierre.isAfter(horaApertura)) {
            throw new DatosInvalidosException(List.of("horarios"));
        }
    }
}
