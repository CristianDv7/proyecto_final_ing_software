package com.cj7.ubicate.domain.exception;

import java.util.List;

/**
 * Se lanza cuando faltan campos obligatorios o hay datos inválidos al registrar
 * un local. Transporta la lista de campos afectados para informar al cliente (HTTP 422).
 */
public class DatosInvalidosException extends RuntimeException {

    private final List<String> campos;

    public DatosInvalidosException(List<String> campos) {
        super("Datos inválidos o campos obligatorios faltantes: " + campos);
        this.campos = List.copyOf(campos);
    }

    public List<String> getCampos() {
        return campos;
    }
}
