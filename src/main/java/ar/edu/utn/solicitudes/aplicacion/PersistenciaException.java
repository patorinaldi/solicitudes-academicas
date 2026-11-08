package ar.edu.utn.solicitudes.aplicacion;

/** No se pudieron leer o guardar los datos. Antes estos errores se tragaban en un catch vacío. */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
