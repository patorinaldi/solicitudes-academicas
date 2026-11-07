package ar.edu.utn.solicitudes.dominio;

/** Base de los errores de negocio. El mensaje está pensado para mostrarse al usuario. */
public abstract class SolicitudException extends RuntimeException {

    protected SolicitudException(String mensaje) {
        super(mensaje);
    }
}
