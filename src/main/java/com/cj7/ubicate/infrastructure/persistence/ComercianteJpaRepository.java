package com.cj7.ubicate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComercianteJpaRepository extends JpaRepository<ComercianteEntity, UUID> {

    Optional<ComercianteEntity> findByWhatsapp(String whatsapp);
}
