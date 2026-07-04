package com.cj7.ubicate.domain.model;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;

import java.util.List;

/**
 * Punto geográfico del local (value object). Valida rango de coordenadas y origen.
 */
public record Ubicacion(double latitud, double longitud, OrigenUbicacion origen, String direccionTexto) {

    public Ubicacion {
        if (origen == null) {
            throw new DatosInvalidosException(List.of("ubicacion.origen"));
        }
        if (latitud < -90 || latitud > 90) {
            throw new DatosInvalidosException(List.of("ubicacion.latitud"));
        }
        if (longitud < -180 || longitud > 180) {
            throw new DatosInvalidosException(List.of("ubicacion.longitud"));
        }
    }
}
