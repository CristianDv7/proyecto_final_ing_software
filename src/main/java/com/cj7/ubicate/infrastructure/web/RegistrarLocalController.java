package com.cj7.ubicate.infrastructure.web;

import com.cj7.ubicate.application.port.in.RegistrarLocalUseCasePort;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.infrastructure.web.generated.api.LocalesApi;
import com.cj7.ubicate.infrastructure.web.generated.model.LocalResponse;
import com.cj7.ubicate.infrastructure.web.generated.model.RegistrarLocalRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** Controlador REST que implementa el contrato OpenAPI generado. */
@RestController
public class RegistrarLocalController implements LocalesApi {

    private final RegistrarLocalUseCasePort registrarLocal;
    private final LocalWebMapper mapper;

    public RegistrarLocalController(RegistrarLocalUseCasePort registrarLocal, LocalWebMapper mapper) {
        this.registrarLocal = registrarLocal;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<LocalResponse> registrarLocal(RegistrarLocalRequest registrarLocalRequest) {
        Local local = registrarLocal.registrar(mapper.aComando(registrarLocalRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aRespuesta(local));
    }
}
