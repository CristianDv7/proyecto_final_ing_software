package com.cj7.ubicate.infrastructure.persistence;

import com.cj7.ubicate.domain.exception.LocalDuplicadoException;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.port.LocalRepositoryPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

/** Adaptador de salida: implementa el puerto de locales sobre Spring Data JPA. */
@Component
public class LocalPersistenceAdapter implements LocalRepositoryPort {

    private final LocalJpaRepository repository;

    public LocalPersistenceAdapter(LocalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Local guardar(Local local) {
        try {
            // saveAndFlush fuerza el INSERT dentro de la transacción para que la
            // restricción única (whatsapp + ubicación) se evalúe aquí y no al commit.
            repository.saveAndFlush(toEntity(local));
        } catch (DataIntegrityViolationException e) {
            // FR-012: otra transacción concurrente ya registró este local (misma
            // identidad y ubicación). La restricción única garantiza que solo una
            // prospere; la perdedora se rechaza como duplicado (409), no como error 500.
            throw new LocalDuplicadoException(
                    "Ya existe un local con este WhatsApp y ubicación equivalente.");
        }
        return local;
    }

    @Override
    public boolean existeDuplicado(String whatsapp, double latitud, double longitud) {
        return repository.existsByWhatsappAndLatitudAndLongitud(whatsapp, latitud, longitud);
    }

    private LocalEntity toEntity(Local local) {
        LocalEntity entity = new LocalEntity();
        entity.setId(local.getId());
        entity.setNombre(local.getNombre());
        entity.setTipoNegocioId(local.getTipoNegocioId());
        entity.setWhatsapp(local.getWhatsapp());
        entity.setLatitud(local.getUbicacion().latitud());
        entity.setLongitud(local.getUbicacion().longitud());
        entity.setOrigen(local.getUbicacion().origen());
        entity.setDireccionTexto(local.getUbicacion().direccionTexto());
        entity.setEstadoPublicacion(local.getEstadoPublicacion());
        entity.setComercianteId(local.getComercianteId());
        entity.setFechaCreacion(local.getFechaCreacion());
        entity.setHorarios(toHorarioEntities(local.getHorarios()));
        entity.setServicios(List.copyOf(local.getServicios()));
        return entity;
    }

    private List<HorarioEmbeddable> toHorarioEntities(List<HorarioAtencion> horarios) {
        return horarios.stream().map(h -> {
            HorarioEmbeddable e = new HorarioEmbeddable();
            e.setDiaSemana(h.diaSemana());
            e.setHoraApertura(h.horaApertura());
            e.setHoraCierre(h.horaCierre());
            e.setCruzaMedianoche(h.cruzaMedianoche());
            return e;
        }).toList();
    }
}
