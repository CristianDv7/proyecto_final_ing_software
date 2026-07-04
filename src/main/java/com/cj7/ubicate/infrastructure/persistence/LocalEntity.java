package com.cj7.ubicate.infrastructure.persistence;

import com.cj7.ubicate.domain.model.EstadoPublicacion;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "local", uniqueConstraints = @UniqueConstraint(
        name = "uq_local_whatsapp_ubicacion", columnNames = {"whatsapp", "latitud", "longitud"}))
@Getter
@Setter
@NoArgsConstructor
public class LocalEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "tipo_negocio_id", nullable = false)
    private Long tipoNegocioId;

    @Column(nullable = false, length = 20)
    private String whatsapp;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrigenUbicacion origen;

    @Column(name = "direccion_texto")
    private String direccionTexto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_publicacion", nullable = false, length = 15)
    private EstadoPublicacion estadoPublicacion;

    @Column(name = "comerciante_id", nullable = false)
    private UUID comercianteId;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "horario_atencion", joinColumns = @JoinColumn(name = "local_id"))
    private List<HorarioEmbeddable> horarios = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "servicio", joinColumns = @JoinColumn(name = "local_id"))
    @Column(name = "nombre")
    private List<String> servicios = new ArrayList<>();
}
