package ar.edu.utn.solicitudes.dominio;

import java.util.Objects;
import java.util.Optional;

/**
 * Una solicitud de un estudiante. Sus reglas viven acá: qué cambios de estado admite y cuándo
 * se le puede asignar un responsable. Nadie puede modificar sus datos desde afuera sin pasar
 * por esas reglas.
 */
public class Solicitud {

    private final int id;
    private final String legajo;
    private final TipoSolicitud tipo;
    private final String descripcion;
    private final String fecha;
    private final Prioridad prioridad;
    private final boolean urgente;
    private EstadoSolicitud estado;
    private String responsable;

    public Solicitud(int id, String legajo, TipoSolicitud tipo, String descripcion, String fecha,
                     Prioridad prioridad, boolean urgente, EstadoSolicitud estado, String responsable) {
        this.id = id;
        this.legajo = Objects.requireNonNull(legajo);
        this.tipo = Objects.requireNonNull(tipo);
        this.descripcion = Objects.requireNonNull(descripcion);
        this.fecha = Objects.requireNonNull(fecha);
        this.prioridad = Objects.requireNonNull(prioridad);
        this.urgente = urgente;
        this.estado = Objects.requireNonNull(estado);
        this.responsable = responsable == null || responsable.isEmpty() ? null : responsable;
    }

    /** Una solicitud recién registrada: pendiente y sin responsable. */
    public static Solicitud nueva(int id, String legajo, TipoSolicitud tipo, String descripcion, String fecha,
                                  Prioridad prioridad, boolean urgente) {
        return new Solicitud(id, legajo, tipo, descripcion, fecha, prioridad, urgente, EstadoSolicitud.PENDIENTE,
                null);
    }

    public void cambiarEstado(EstadoSolicitud nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new TransicionInvalidaException("no se puede pasar de " + estado + " a " + nuevo);
        }
        if (urgente && nuevo == EstadoSolicitud.RECHAZADA) {
            throw new TransicionInvalidaException("una solicitud urgente no se puede rechazar");
        }
        estado = nuevo;
    }

    /** Asigna un responsable del área que corresponde. Una solicitud pendiente pasa a estar en proceso. */
    public void asignar(Responsable nuevoResponsable) {
        if (nuevoResponsable.area() != tipo.area()) {
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
        return tipo;
    }

    public String descripcion() {
        return descripcion;
    }

    public String fecha() {
        return fecha;
    }

    public Prioridad prioridad() {
        return prioridad;
    }

    public boolean esUrgente() {
        return urgente;
    }

    public EstadoSolicitud estado() {
        return estado;
    }

    public Optional<String> responsable() {
        return Optional.ofNullable(responsable);
    }
}
