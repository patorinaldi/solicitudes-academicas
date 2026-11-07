package ar.edu.utn.solicitudes;

public class SolicitudCerradaException extends SolicitudException {

    public SolicitudCerradaException() {
        super("la solicitud esta cerrada");
    }
}
