package com.cj7.ubicate.infrastructure.persistence;

import com.cj7.ubicate.domain.port.TipoNegocioRepositoryPort;
import org.springframework.stereotype.Component;

/** Adaptador de salida: valida el catálogo de tipos de negocio. */
@Component
public class TipoNegocioPersistenceAdapter implements TipoNegocioRepositoryPort {

    private final TipoNegocioJpaRepository repository;

    public TipoNegocioPersistenceAdapter(TipoNegocioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
