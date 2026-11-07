package ar.edu.utn.solicitudes;

public class ResponsableInexistenteException extends SolicitudException {

    public ResponsableInexistenteException() {
        super("no existe el responsable");
    }
}
