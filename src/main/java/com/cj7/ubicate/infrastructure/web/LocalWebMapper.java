package com.cj7.ubicate.infrastructure.web;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import com.cj7.ubicate.infrastructure.web.generated.model.LocalResponse;
import com.cj7.ubicate.infrastructure.web.generated.model.RegistrarLocalRequest;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Traduce entre los DTOs generados por OpenAPI (transporte) y el modelo de dominio.
 * Punto único de mapeo (DRY); no filtra tipos de web hacia el dominio.
 */
@Component
public class LocalWebMapper {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public NuevoLocal aComando(RegistrarLocalRequest request) {
        return new NuevoLocal(
                request.getNombre(),
                request.getTipoNegocioId(),
                aUbicacionDominio(request.getUbicacion()),
                request.getWhatsapp(),
                aHorariosDominio(request.getHorarios()),
                request.getServicios());
    }

    private Ubicacion aUbicacionDominio(com.cj7.ubicate.infrastructure.web.generated.model.Ubicacion u) {
        if (u == null) {
            return null;
        }
        return new Ubicacion(
                u.getLatitud(),
                u.getLongitud(),
                OrigenUbicacion.valueOf(u.getOrigen().name()),
                u.getDireccionTexto());
    }

    private List<HorarioAtencion> aHorariosDominio(
            List<com.cj7.ubicate.infrastructure.web.generated.model.HorarioAtencion> horarios) {
        if (horarios == null) {
            return List.of();
        }
        return horarios.stream().map(h -> new HorarioAtencion(
                DiaSemana.valueOf(h.getDiaSemana().name()),
                parseHora(h.getHoraApertura(), "horarios.horaApertura"),
                parseHora(h.getHoraCierre(), "horarios.horaCierre"),
                Boolean.TRUE.equals(h.getCruzaMedianoche()))).toList();
    }

    private LocalTime parseHora(String valor, String campo) {
        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DatosInvalidosException(List.of(campo));
        }
    }

    public LocalResponse aRespuesta(Local local) {
        com.cj7.ubicate.infrastructure.web.generated.model.Ubicacion ubicacion =
                new com.cj7.ubicate.infrastructure.web.generated.model.Ubicacion(
                        local.getUbicacion().latitud(),
                        local.getUbicacion().longitud(),
                        com.cj7.ubicate.infrastructure.web.generated.model.Ubicacion.OrigenEnum
                                .valueOf(local.getUbicacion().origen().name()));
        ubicacion.setDireccionTexto(local.getUbicacion().direccionTexto());

        List<com.cj7.ubicate.infrastructure.web.generated.model.HorarioAtencion> horarios =
                local.getHorarios().stream().map(h -> {
                    var dto = new com.cj7.ubicate.infrastructure.web.generated.model.HorarioAtencion(
                            com.cj7.ubicate.infrastructure.web.generated.model.HorarioAtencion.DiaSemanaEnum
                                    .valueOf(h.diaSemana().name()),
                            h.horaApertura().format(HORA),
                            h.horaCierre().format(HORA));
                    dto.setCruzaMedianoche(h.cruzaMedianoche());
                    return dto;
                }).toList();

        LocalResponse respuesta = new LocalResponse(
                local.getId(),
                local.getNombre(),
                local.getTipoNegocioId(),
                ubicacion,
                local.getWhatsapp(),
                horarios,
                LocalResponse.EstadoPublicacionEnum.valueOf(local.getEstadoPublicacion().name()));
        respuesta.setServicios(local.getServicios());
        return respuesta;
    }
}
