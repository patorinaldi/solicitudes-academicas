package ar.edu.utn.solicitudes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TipoSolicitudTest {

    @Test
    @DisplayName("Cada tipo conoce su área y su prioridad base")
    void areaYPrioridadPorTipo() {
        assertEquals(Area.ALUMNOS, TipoSolicitud.CONSTANCIA.area());
        assertEquals(Area.BEDELIA, TipoSolicitud.EXAMEN.area());
        assertEquals(Area.SECRETARIA_ACADEMICA, TipoSolicitud.EQUIVALENCIA.area());
        assertEquals(Area.ALUMNOS, TipoSolicitud.RECLAMO.area());
        assertEquals(Prioridad.BAJA, TipoSolicitud.CONSTANCIA.prioridadBase());
        assertEquals(Prioridad.ALTA, TipoSolicitud.EXAMEN.prioridadBase());
        assertEquals(Prioridad.MEDIA, TipoSolicitud.EQUIVALENCIA.prioridadBase());
        assertEquals(Prioridad.MEDIA, TipoSolicitud.RECLAMO.prioridadBase());
    }

    @Test
    @DisplayName("Interpreta el tipo escrito por el usuario y rechaza los desconocidos")
    void desdeTexto() {
        assertEquals(Optional.of(TipoSolicitud.EQUIVALENCIA), TipoSolicitud.desde(" equivalencia"));
        assertEquals(Optional.empty(), TipoSolicitud.desde("CERTIFICADO"));
        assertEquals(Optional.empty(), TipoSolicitud.desde(""));
    }
}
