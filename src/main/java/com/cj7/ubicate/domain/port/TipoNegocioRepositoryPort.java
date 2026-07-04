package com.cj7.ubicate.domain.port;

/** Puerto de salida para validar el catálogo de tipos de negocio. */
public interface TipoNegocioRepositoryPort {

    boolean existePorId(Long id);
}
