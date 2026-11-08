package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.DatosInvalidosException;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Valida los datos de una solicitud nueva, en el orden en que se informan los errores. */
public class ValidadorSolicitud {

    public static final int LARGO_MAXIMO_DESCRIPCION = 300;

    /** El archivo usa ; como separador: una descripción con ; corrompería la fila. */
    private static final String SEPARADOR_PROHIBIDO = ";";

    public void validar(NuevaSolicitud datos) {
        validarLegajo(datos.legajo());
        validarNombre(datos.nombre());
        validarEmail(datos.email());
        validarTipo(datos.tipo());
        validarDescripcion(datos.descripcion());
        validarFecha(datos);
    }

    private static void validarLegajo(String legajo) {
        if (legajo == null || !legajo.trim().matches("\\d{5}")) {
            throw new DatosInvalidosException("el legajo debe tener 5 digitos");
        }
    }

    private static void validarNombre(String nombre) {
        if (esVacio(nombre)) {
            throw new DatosInvalidosException("el nombre es obligatorio");
        }
    }

    private static void validarEmail(String email) {
        if (!esEmailValido(email)) {
            throw new DatosInvalidosException("email invalido");
        }
    }

    private static void validarTipo(String tipo) {
        if (TipoSolicitud.desde(tipo).isEmpty()) {
            throw new DatosInvalidosException("tipo de solicitud invalido");
        }
    }

    private static void validarDescripcion(String descripcion) {
        if (esVacio(descripcion) || descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
            throw new DatosInvalidosException(
                    "la descripcion es obligatoria y no puede superar los " + LARGO_MAXIMO_DESCRIPCION + " caracteres");
        }
        if (descripcion.contains(SEPARADOR_PROHIBIDO)) {
            throw new DatosInvalidosException("la descripcion no puede contener ';'");
        }
    }

    private static void validarFecha(NuevaSolicitud datos) {
        if (datos.fecha() == null || !datos.fecha().trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new DatosInvalidosException("la fecha debe tener el formato dd/mm/aaaa");
        }
        if (fecha(datos).isEmpty()) {
            throw new DatosInvalidosException("la fecha " + datos.fecha().trim() + " no existe");
        }
    }

    /** La fecha ya validada; vacío si el formato es correcto pero el día no existe (31/02). */
    static Optional<LocalDate> fecha(NuevaSolicitud datos) {
        try {
            return Optional.of(LocalDate.parse(datos.fecha().trim(), Solicitud.FORMATO_FECHA));
        } catch (DateTimeParseException e) {
            return Optional.empty();
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
