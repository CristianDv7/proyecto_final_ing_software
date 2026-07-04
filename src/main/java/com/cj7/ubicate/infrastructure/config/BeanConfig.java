package com.cj7.ubicate.infrastructure.config;

import com.cj7.ubicate.application.port.in.RegistrarLocalUseCasePort;
import com.cj7.ubicate.application.usecase.RegistrarLocalService;
import com.cj7.ubicate.domain.port.ComercianteRepositoryPort;
import com.cj7.ubicate.domain.port.LocalRepositoryPort;
import com.cj7.ubicate.domain.port.TipoNegocioRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wiring de la capa de aplicación con los puertos de salida (inversión de dependencias). */
@Configuration
public class BeanConfig {

    @Bean
    public RegistrarLocalUseCasePort registrarLocalUseCasePort(LocalRepositoryPort localRepository,
                                                               ComercianteRepositoryPort comercianteRepository,
                                                               TipoNegocioRepositoryPort tipoNegocioRepository) {
        return new RegistrarLocalService(localRepository, comercianteRepository, tipoNegocioRepository);
    }
}
