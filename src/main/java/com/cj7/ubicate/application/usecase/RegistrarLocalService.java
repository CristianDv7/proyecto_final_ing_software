package com.cj7.ubicate.application.usecase;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.application.port.in.RegistrarLocalUseCasePort;
import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.exception.LocalDuplicadoException;
import com.cj7.ubicate.domain.model.Comerciante;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.port.ComercianteRepositoryPort;
import com.cj7.ubicate.domain.port.LocalRepositoryPort;
import com.cj7.ubicate.domain.port.TipoNegocioRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso de registro de local. Valida el catálogo, detecta duplicados,
 * resuelve el comerciante por WhatsApp (crea o reutiliza) y publica el local de
 * forma atómica (transaccional).
 */
public class RegistrarLocalService implements RegistrarLocalUseCasePort {

    private final LocalRepositoryPort localRepository;
    private final ComercianteRepositoryPort comercianteRepository;
    private final TipoNegocioRepositoryPort tipoNegocioRepository;

    public RegistrarLocalService(LocalRepositoryPort localRepository,
                                 ComercianteRepositoryPort comercianteRepository,
                                 TipoNegocioRepositoryPort tipoNegocioRepository) {
        this.localRepository = localRepository;
        this.comercianteRepository = comercianteRepository;
        this.tipoNegocioRepository = tipoNegocioRepository;
    }

    @Override
    @Transactional
    public Local registrar(NuevoLocal comando) {
        if (comando.tipoNegocioId() == null || !tipoNegocioRepository.existePorId(comando.tipoNegocioId())) {
            throw new DatosInvalidosException(List.of("tipoNegocioId"));
        }

        if (comando.ubicacion() != null && comando.whatsapp() != null
                && localRepository.existeDuplicado(comando.whatsapp(),
                comando.ubicacion().latitud(), comando.ubicacion().longitud())) {
            throw new LocalDuplicadoException(
                    "Ya existe un local con este WhatsApp y ubicación equivalente.");
        }

        Comerciante comerciante = comercianteRepository.buscarPorWhatsapp(comando.whatsapp())
                .orElseGet(() -> comercianteRepository.guardar(Comerciante.nuevo(comando.whatsapp())));

        Local local = Local.publicar(comando.nombre(), comando.tipoNegocioId(), comando.ubicacion(),
                comando.whatsapp(), comando.horarios(), comando.servicios(), comerciante.id());

        return localRepository.guardar(local);
    }
}
