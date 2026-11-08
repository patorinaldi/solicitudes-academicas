package ar.edu.utn.solicitudes.dominio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Objects;
import java.util.Optional;

/**
 * Una solicitud de un estudiante. Sus reglas viven acá: qué cambios de estado admite y cuándo
 * se le puede asignar un responsable. Nadie puede modificar sus datos desde afuera sin pasar
 * por esas reglas.
 */
public class Solicitud {

    /** Formato en que se ingresa, se muestra y se guarda la fecha. STRICT rechaza el 31/02. */
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final int id;
    private final String legajo;
    private final DetalleSolicitud detalle;
    private EstadoSolicitud estado;
    private String responsable;

    public Solicitud(int id, String legajo, DetalleSolicitud detalle, EstadoSolicitud estado, String responsable) {
        this.id = id;
        this.legajo = Objects.requireNonNull(legajo);
        this.detalle = Objects.requireNonNull(detalle);
        this.estado = Objects.requireNonNull(estado);
        this.responsable = responsable == null || responsable.isEmpty() ? null : responsable;
    }

    /** Una solicitud recién registrada: pendiente y sin responsable. */
    public static Solicitud nueva(int id, String legajo, DetalleSolicitud detalle) {
        return new Solicitud(id, legajo, detalle, EstadoSolicitud.PENDIENTE, null);
    }

    public void cambiarEstado(EstadoSolicitud nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new TransicionInvalidaException("no se puede pasar de " + estado + " a " + nuevo);
        }
        if (detalle.urgente() && nuevo == EstadoSolicitud.RECHAZADA) {
            throw new TransicionInvalidaException("una solicitud urgente no se puede rechazar");
        }
        estado = nuevo;
    }

    /** Asigna un responsable del área que corresponde. Una solicitud pendiente pasa a estar en proceso. */
    public void asignar(Responsable nuevoResponsable) {
        if (nuevoResponsable.area() != detalle.tipo().area()) {
            throw new AreaIncorrectaException();
        }
        if (estado.estaCerrado()) {
            throw new SolicitudCerradaException();
        }
        responsable = nuevoResponsable.nombre();
        if (estado == EstadoSolicitud.PENDIENTE) {
            estado = EstadoSolicitud.EN_PROCESO;
        }
    }

    public boolean estaAbiertaSinResponsable() {
        return responsable == null && !estado.estaCerrado();
    }

    public int id() {
        return id;
    }

    public String legajo() {
        return legajo;
    }

    public TipoSolicitud tipo() {
        return detalle.tipo();
    }

    public String descripcion() {
        return detalle.descripcion();
    }

    public LocalDate fecha() {
        return detalle.fecha();
    }

    public Prioridad prioridad() {
        return detalle.prioridad();
    }

    public boolean esUrgente() {
        return detalle.urgente();
    }

    public EstadoSolicitud estado() {
        return estado;
    }

    public Optional<String> responsable() {
        return Optional.ofNullable(responsable);
    }
}
