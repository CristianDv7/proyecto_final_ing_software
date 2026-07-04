package com.cj7.ubicate.infrastructure.persistence;

import com.cj7.ubicate.domain.model.Comerciante;
import com.cj7.ubicate.domain.port.ComercianteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador de salida: resuelve y persiste comerciantes sobre Spring Data JPA. */
@Component
public class ComerciantePersistenceAdapter implements ComercianteRepositoryPort {

    private final ComercianteJpaRepository repository;

    public ComerciantePersistenceAdapter(ComercianteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Comerciante> buscarPorWhatsapp(String whatsapp) {
        return repository.findByWhatsapp(whatsapp)
                .map(e -> new Comerciante(e.getId(), e.getWhatsapp(), e.getFechaRegistro()));
    }

    @Override
    public Comerciante guardar(Comerciante comerciante) {
        ComercianteEntity entity = new ComercianteEntity();
        entity.setId(comerciante.id());
        entity.setWhatsapp(comerciante.whatsapp());
        entity.setFechaRegistro(comerciante.fechaRegistro());
        repository.save(entity);
        return comerciante;
    }
}
