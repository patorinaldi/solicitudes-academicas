package ar.edu.utn.solicitudes.aplicacion;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FiltroSolicitudesTest {

    private final Solicitud examen = Solicitud.nueva(2, "10002", TipoSolicitud.EXAMEN, "Mesa especial",
            LocalDate.of(2026, 3, 10), Prioridad.ALTA, false);

    @Test
    @DisplayName("Un filtro sin criterios admite todo")
    void sinCriterios() {
        assertTrue(new FiltroSolicitudes("", " ", null, "").admite(examen));
    }

    @Test
    @DisplayName("Tipo, estado y prioridad no distinguen mayúsculas")
    void criteriosSinMayusculas() {
        assertTrue(new FiltroSolicitudes("examen", "pendiente", "Alta", "").admite(examen));
        assertFalse(new FiltroSolicitudes("RECLAMO", "", "", "").admite(examen));
    }

    @Test
    @DisplayName("El legajo tiene que coincidir exactamente")
    void legajoExacto() {
        assertTrue(new FiltroSolicitudes("", "", "", " 10002 ").admite(examen));
        assertFalse(new FiltroSolicitudes("", "", "", "1000").admite(examen));
    }
}
