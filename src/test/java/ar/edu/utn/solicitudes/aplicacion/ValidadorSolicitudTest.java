package ar.edu.utn.solicitudes.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.solicitudes.dominio.DatosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidadorSolicitudTest {

    private final ValidadorSolicitud validador = new ValidadorSolicitud();

    private static NuevaSolicitud valida() {
        return new NuevaSolicitud("10001", "Ana Torres", "ana@example.edu", "TUP", "CONSTANCIA",
                "Constancia de alumno regular", "03/03/2026", false);
    }

    private String errorDe(NuevaSolicitud datos) {
        return assertThrows(DatosInvalidosException.class, () -> validador.validar(datos)).getMessage();
    }

    @Test
    @DisplayName("Acepta una solicitud con todos los datos correctos")
    void solicitudValida() {
        validador.validar(valida());
    }

    @Test
    @DisplayName("El legajo debe tener exactamente 5 dígitos")
    void legajo() {
        NuevaSolicitud datos = valida();
        assertEquals("el legajo debe tener 5 digitos",
                errorDe(new NuevaSolicitud("1234", datos.nombre(), datos.email(), datos.carrera(), datos.tipo(),
                        datos.descripcion(), datos.fecha(), false)));
        assertEquals("el legajo debe tener 5 digitos",
                errorDe(new NuevaSolicitud("12a45", datos.nombre(), datos.email(), datos.carrera(), datos.tipo(),
                        datos.descripcion(), datos.fecha(), false)));
    }

    @Test
    @DisplayName("El email necesita algo antes de la arroba y un punto después")
    void email() {
        NuevaSolicitud d = valida();
        for (String email : new String[] {"ana.example.edu", "@example.edu", "ana@example"}) {
            assertEquals("email invalido", errorDe(new NuevaSolicitud(d.legajo(), d.nombre(), email, d.carrera(),
                    d.tipo(), d.descripcion(), d.fecha(), false)));
        }
    }

    @Test
    @DisplayName("Valor límite: 300 caracteres de descripción se aceptan, 301 no")
    void largoDeLaDescripcion() {
        NuevaSolicitud d = valida();
        validador.validar(new NuevaSolicitud(d.legajo(), d.nombre(), d.email(), d.carrera(), d.tipo(),
                "x".repeat(300), d.fecha(), false));
        assertEquals("la descripcion es obligatoria y no puede superar los 300 caracteres",
                errorDe(new NuevaSolicitud(d.legajo(), d.nombre(), d.email(), d.carrera(), d.tipo(),
                        "x".repeat(301), d.fecha(), false)));
    }

    @Test
    @DisplayName("Corrección D1: rechaza una fecha con formato correcto que no existe")
    void fechaInexistente() {
        NuevaSolicitud d = valida();
        assertEquals("la fecha 31/02/2026 no existe", errorDe(new NuevaSolicitud(d.legajo(), d.nombre(), d.email(),
                d.carrera(), d.tipo(), d.descripcion(), "31/02/2026", false)));
        validador.validar(new NuevaSolicitud(d.legajo(), d.nombre(), d.email(), d.carrera(), d.tipo(),
                d.descripcion(), "29/02/2028", false));
    }

    @Test
    @DisplayName("Corrección D2: la descripción no puede contener el separador del archivo")
    void descripcionConSeparador() {
        NuevaSolicitud d = valida();
        assertEquals("la descripcion no puede contener ';'", errorDe(new NuevaSolicitud(d.legajo(), d.nombre(),
                d.email(), d.carrera(), d.tipo(), "Nota de Algebra; no figura en actas", d.fecha(), false)));
    }

    @Test
    @DisplayName("Informa el primer error en el orden de la versión inicial")
    void ordenDeLosErrores() {
        NuevaSolicitud todoMal = new NuevaSolicitud("1", "", "x", "", "OTRO", "", "ayer", false);

        assertEquals("el legajo debe tener 5 digitos", errorDe(todoMal));
    }
}
