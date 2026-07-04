package com.cj7.ubicate.domain.model;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad raíz del registro. Un local se crea ya publicado cuando sus campos
 * obligatorios están completos (nombre, tipo de negocio, ubicación, WhatsApp y
 * al menos un horario). Los servicios son opcionales.
 */
public final class Local {

    private final UUID id;
    private final String nombre;
    private final Long tipoNegocioId;
    private final Ubicacion ubicacion;
    private final String whatsapp;
    private final List<HorarioAtencion> horarios;
    private final List<String> servicios;
    private final EstadoPublicacion estadoPublicacion;
    private final UUID comercianteId;
    private final Instant fechaCreacion;

    private Local(UUID id, String nombre, Long tipoNegocioId, Ubicacion ubicacion, String whatsapp,
                  List<HorarioAtencion> horarios, List<String> servicios, EstadoPublicacion estadoPublicacion,
                  UUID comercianteId, Instant fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.tipoNegocioId = tipoNegocioId;
        this.ubicacion = ubicacion;
        this.whatsapp = whatsapp;
        this.horarios = horarios;
        this.servicios = servicios;
        this.estadoPublicacion = estadoPublicacion;
        this.comercianteId = comercianteId;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Crea y publica un local nuevo validando los campos obligatorios. Lanza
     * {@link DatosInvalidosException} con los campos faltantes si el registro es incompleto.
     */
    public static Local publicar(String nombre, Long tipoNegocioId, Ubicacion ubicacion, String whatsapp,
                                 List<HorarioAtencion> horarios, List<String> servicios, UUID comercianteId) {
        List<String> faltantes = new ArrayList<>();
        if (nombre == null || nombre.isBlank()) {
            faltantes.add("nombre");
        }
        if (tipoNegocioId == null) {
            faltantes.add("tipoNegocioId");
        }
        if (ubicacion == null) {
            faltantes.add("ubicacion");
        }
        if (whatsapp == null || whatsapp.isBlank()) {
            faltantes.add("whatsapp");
        }
        if (horarios == null || horarios.isEmpty()) {
            faltantes.add("horarios");
        }
        if (!faltantes.isEmpty()) {
            throw new DatosInvalidosException(faltantes);
        }
        return new Local(UUID.randomUUID(), nombre, tipoNegocioId, ubicacion, whatsapp,
                List.copyOf(horarios), servicios == null ? List.of() : List.copyOf(servicios),
                EstadoPublicacion.PUBLICADO, comercianteId, Instant.now());
    }

    /** Reconstruye un local ya existente desde la capa de persistencia (sin revalidar). */
    public static Local reconstruir(UUID id, String nombre, Long tipoNegocioId, Ubicacion ubicacion,
                                    String whatsapp, List<HorarioAtencion> horarios, List<String> servicios,
                                    EstadoPublicacion estadoPublicacion, UUID comercianteId, Instant fechaCreacion) {
        return new Local(id, nombre, tipoNegocioId, ubicacion, whatsapp,
                List.copyOf(horarios), List.copyOf(servicios), estadoPublicacion, comercianteId, fechaCreacion);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getTipoNegocioId() {
        return tipoNegocioId;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public List<HorarioAtencion> getHorarios() {
        return horarios;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public EstadoPublicacion getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public UUID getComercianteId() {
        return comercianteId;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}
