package ar.edu.utn.solicitudes.dominio;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Lo que se pidió: datos fijos de una solicitud desde que se registra. Agrupa los cinco valores
 * que viajaban sueltos en el constructor de {@link Solicitud} (observación CR-01 de la revisión).
 */
public record DetalleSolicitud(TipoSolicitud tipo, String descripcion, LocalDate fecha, Prioridad prioridad,
                               boolean urgente) {

    public DetalleSolicitud {
        Objects.requireNonNull(tipo);
        Objects.requireNonNull(descripcion);
        Objects.requireNonNull(fecha);
        Objects.requireNonNull(prioridad);
    }
}
