package ar.edu.utn.solicitudes;

/** Base de los errores de negocio. El mensaje está pensado para mostrarse al usuario. */
public abstract class SolicitudException extends RuntimeException {

    protected SolicitudException(String mensaje) {
        super(mensaje);
    }
}
