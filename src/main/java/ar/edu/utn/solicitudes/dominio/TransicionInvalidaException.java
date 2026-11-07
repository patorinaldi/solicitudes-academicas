package ar.edu.utn.solicitudes.dominio;

public class TransicionInvalidaException extends SolicitudException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
