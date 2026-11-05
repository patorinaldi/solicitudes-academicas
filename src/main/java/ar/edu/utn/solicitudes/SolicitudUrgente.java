package ar.edu.utn.solicitudes;

// Las solicitudes urgentes no se pueden rechazar.
public class SolicitudUrgente extends Solicitud {

    @Override
    public void cambiarEstado(String nuevo) {
        if (nuevo.equals("RECHAZADA")) {
            throw new UnsupportedOperationException("Una solicitud urgente no se puede rechazar");
        }
        this.estado = nuevo;
    }
}
