package ar.edu.utn.solicitudes.dominio;

public class ResponsableInexistenteException extends SolicitudException {

    public ResponsableInexistenteException() {
        super("no existe el responsable");
    }
}
