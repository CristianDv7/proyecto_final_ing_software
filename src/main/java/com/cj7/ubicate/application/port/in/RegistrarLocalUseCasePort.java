package com.cj7.ubicate.application.port.in;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.domain.model.Local;

/** Puerto de entrada: registrar y publicar un local. */
public interface RegistrarLocalUseCasePort {

    Local registrar(NuevoLocal comando);
}
