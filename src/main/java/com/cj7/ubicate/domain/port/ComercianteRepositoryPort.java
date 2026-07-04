package com.cj7.ubicate.domain.port;

import com.cj7.ubicate.domain.model.Comerciante;

import java.util.Optional;

/** Puerto de salida para resolver y persistir comerciantes por su WhatsApp. */
public interface ComercianteRepositoryPort {

    Optional<Comerciante> buscarPorWhatsapp(String whatsapp);

    Comerciante guardar(Comerciante comerciante);
}
