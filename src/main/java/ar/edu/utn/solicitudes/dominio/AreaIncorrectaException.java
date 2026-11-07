package ar.edu.utn.solicitudes.dominio;

public class AreaIncorrectaException extends SolicitudException {

    public AreaIncorrectaException() {
        super("el responsable no pertenece al area de la solicitud");
    }
}
