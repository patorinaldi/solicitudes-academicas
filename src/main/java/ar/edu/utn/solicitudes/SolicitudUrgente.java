package ar.edu.utn.solicitudes;

// Las solicitudes urgentes no se pueden rechazar.
public class SolicitudUrgente extends Solicitud {

    @Override
    public void cambiarEstado(EstadoSolicitud nuevo) {
        if (nuevo == EstadoSolicitud.RECHAZADA) {
            throw new UnsupportedOperationException("Una solicitud urgente no se puede rechazar");
        }
        this.estado = nuevo;
    }
}
