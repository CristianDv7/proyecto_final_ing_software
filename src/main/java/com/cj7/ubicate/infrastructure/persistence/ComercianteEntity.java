package com.cj7.ubicate.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comerciante")
@Getter
@Setter
@NoArgsConstructor
public class ComercianteEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String whatsapp;

    @Column(name = "fecha_registro", nullable = false)
    private Instant fechaRegistro;
}
