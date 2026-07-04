package com.cj7.ubicate.application.command;

import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Ubicacion;

import java.util.List;

/**
 * Comando de entrada al caso de uso de registro (datos ya traducidos a dominio,
 * independientes del transporte HTTP).
 */
public record NuevoLocal(String nombre, Long tipoNegocioId, Ubicacion ubicacion, String whatsapp,
                         List<HorarioAtencion> horarios, List<String> servicios) {
}
