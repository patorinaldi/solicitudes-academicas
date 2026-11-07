package ar.edu.utn.solicitudes.dominio;

public class SolicitudCerradaException extends SolicitudException {

    public SolicitudCerradaException() {
        super("la solicitud esta cerrada");
    }
}
