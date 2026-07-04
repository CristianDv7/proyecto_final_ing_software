package com.cj7.ubicate.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_negocio")
@Getter
@Setter
@NoArgsConstructor
public class TipoNegocioEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
}
