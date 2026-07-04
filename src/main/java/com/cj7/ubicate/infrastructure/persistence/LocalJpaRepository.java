package com.cj7.ubicate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocalJpaRepository extends JpaRepository<LocalEntity, UUID> {

    boolean existsByWhatsappAndLatitudAndLongitud(String whatsapp, double latitud, double longitud);
}
