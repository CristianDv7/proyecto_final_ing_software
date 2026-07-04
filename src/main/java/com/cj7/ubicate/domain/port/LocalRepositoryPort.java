package com.cj7.ubicate.domain.port;

import com.cj7.ubicate.domain.model.Local;

/** Puerto de salida para persistir y consultar locales. */
public interface LocalRepositoryPort {

    Local guardar(Local local);

    boolean existeDuplicado(String whatsapp, double latitud, double longitud);
}
