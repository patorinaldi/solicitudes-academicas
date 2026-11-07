package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.DatosInvalidosException;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;

/** Valida los datos de una solicitud nueva, en el orden en que se informan los errores. */
public class ValidadorSolicitud {

    public static final int LARGO_MAXIMO_DESCRIPCION = 300;

    public void validar(NuevaSolicitud datos) {
        if (datos.legajo() == null || !datos.legajo().trim().matches("\\d{5}")) {
            throw new DatosInvalidosException("el legajo debe tener 5 digitos");
        }
        if (esVacio(datos.nombre())) {
            throw new DatosInvalidosException("el nombre es obligatorio");
        }
        if (!esEmailValido(datos.email())) {
            throw new DatosInvalidosException("email invalido");
        }
        if (TipoSolicitud.desde(datos.tipo()).isEmpty()) {
            throw new DatosInvalidosException("tipo de solicitud invalido");
        }
        if (esVacio(datos.descripcion()) || datos.descripcion().length() > LARGO_MAXIMO_DESCRIPCION) {
            throw new DatosInvalidosException(
                    "la descripcion es obligatoria y no puede superar los " + LARGO_MAXIMO_DESCRIPCION + " caracteres");
        }
        if (datos.fecha() == null || !datos.fecha().trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new DatosInvalidosException("la fecha debe tener el formato dd/mm/aaaa");
        }
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /** Hay algo antes de la arroba y un punto en algún lugar después de ella. */
    private static boolean esEmailValido(String email) {
        if (email == null) {
            return false;
        }
        int arroba = email.indexOf('@');
        return arroba > 0 && email.indexOf('.', arroba) >= 0;
    }
}
