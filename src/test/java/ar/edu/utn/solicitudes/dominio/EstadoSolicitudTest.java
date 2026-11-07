package ar.edu.utn.solicitudes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EstadoSolicitudTest {

    @Test
    @DisplayName("Una solicitud pendiente puede pasar a en proceso o ser rechazada, pero no resolverse")
    void transicionesDesdePendiente() {
        // Arrange
        EstadoSolicitud pendiente = EstadoSolicitud.PENDIENTE;

        // Act + Assert (tres consultas sobre el mismo estado)
        assertTrue(pendiente.puedePasarA(EstadoSolicitud.EN_PROCESO));
        assertTrue(pendiente.puedePasarA(EstadoSolicitud.RECHAZADA));
        assertFalse(pendiente.puedePasarA(EstadoSolicitud.RESUELTA));
    }

    @Test
    @DisplayName("Una solicitud en proceso puede resolverse o rechazarse, pero no volver a pendiente")
    void transicionesDesdeEnProceso() {
        EstadoSolicitud enProceso = EstadoSolicitud.EN_PROCESO;

        assertTrue(enProceso.puedePasarA(EstadoSolicitud.RESUELTA));
        assertTrue(enProceso.puedePasarA(EstadoSolicitud.RECHAZADA));
        assertFalse(enProceso.puedePasarA(EstadoSolicitud.PENDIENTE));
    }

    @Test
    @DisplayName("Los estados cerrados no admiten ninguna transición")
    void losEstadosCerradosSonFinales() {
        for (EstadoSolicitud cerrado : new EstadoSolicitud[] {EstadoSolicitud.RESUELTA, EstadoSolicitud.RECHAZADA}) {
            assertTrue(cerrado.estaCerrado());
            for (EstadoSolicitud destino : EstadoSolicitud.values()) {
                assertFalse(cerrado.puedePasarA(destino), cerrado + " -> " + destino);
            }
        }
    }

    @Test
    @DisplayName("Interpreta el texto del usuario sin distinguir mayúsculas ni espacios")
    void desdeTexto() {
        assertEquals(Optional.of(EstadoSolicitud.EN_PROCESO), EstadoSolicitud.desde("  en_proceso "));
        assertEquals(Optional.empty(), EstadoSolicitud.desde("CERRADA"));
        assertEquals(Optional.empty(), EstadoSolicitud.desde(null));
    }
}
