package ar.edu.utn.solicitudes.dominio;

public class SolicitudInexistenteException extends SolicitudException {

    public SolicitudInexistenteException(int id) {
        super("no existe la solicitud " + id);
    }
}
