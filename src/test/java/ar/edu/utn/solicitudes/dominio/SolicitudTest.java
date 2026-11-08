package ar.edu.utn.solicitudes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SolicitudTest {

    private static Solicitud nueva(TipoSolicitud tipo, boolean urgente) {
        return Solicitud.nueva(1, "10001",
                new DetalleSolicitud(tipo, "Descripcion", LocalDate.of(2026, 3, 3), Prioridad.MEDIA, urgente));
    }

    @Test
    @DisplayName("Una solicitud nueva queda pendiente y sin responsable")
    void nuevaQuedaPendiente() {
        Solicitud solicitud = nueva(TipoSolicitud.CONSTANCIA, false);

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.estado());
        assertEquals(Optional.empty(), solicitud.responsable());
        assertTrue(solicitud.estaAbiertaSinResponsable());
    }

    @Test
    @DisplayName("Cambia de estado cuando la transición es válida")
    void cambioDeEstadoValido() {
        // Arrange
        Solicitud solicitud = nueva(TipoSolicitud.CONSTANCIA, false);

        // Act
        solicitud.cambiarEstado(EstadoSolicitud.EN_PROCESO);
        solicitud.cambiarEstado(EstadoSolicitud.RESUELTA);

        // Assert
        assertEquals(EstadoSolicitud.RESUELTA, solicitud.estado());
    }

    @Test
    @DisplayName("Rechaza una transición inválida y conserva el estado")
    void cambioDeEstadoInvalido() {
        Solicitud solicitud = nueva(TipoSolicitud.CONSTANCIA, false);

        assertThrows(TransicionInvalidaException.class, () -> solicitud.cambiarEstado(EstadoSolicitud.RESUELTA));
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.estado());
    }

    @Test
    @DisplayName("Una solicitud urgente no se puede rechazar (antes era la subclase SolicitudUrgente)")
    void urgenteNoSePuedeRechazar() {
        Solicitud urgente = nueva(TipoSolicitud.RECLAMO, true);

        TransicionInvalidaException error = assertThrows(TransicionInvalidaException.class,
                () -> urgente.cambiarEstado(EstadoSolicitud.RECHAZADA));

        assertEquals("una solicitud urgente no se puede rechazar", error.getMessage());
        assertEquals(EstadoSolicitud.PENDIENTE, urgente.estado());
    }

    @Test
    @DisplayName("Asignar un responsable del área correcta pasa la solicitud pendiente a en proceso")
    void asignarResponsable() {
        Solicitud solicitud = nueva(TipoSolicitud.EXAMEN, false);

        solicitud.asignar(new Responsable("Martin Paz", Area.BEDELIA));

        assertEquals(Optional.of("Martin Paz"), solicitud.responsable());
        assertEquals(EstadoSolicitud.EN_PROCESO, solicitud.estado());
        assertFalse(solicitud.estaAbiertaSinResponsable());
    }

    @Test
    @DisplayName("No acepta un responsable de otra área")
    void responsableDeOtraArea() {
        Solicitud solicitud = nueva(TipoSolicitud.EXAMEN, false);

        assertThrows(AreaIncorrectaException.class,
                () -> solicitud.asignar(new Responsable("Laura Gomez", Area.ALUMNOS)));
        assertEquals(Optional.empty(), solicitud.responsable());
    }

    @Test
    @DisplayName("No se asigna responsable a una solicitud cerrada")
    void solicitudCerrada() {
        Solicitud solicitud = nueva(TipoSolicitud.CONSTANCIA, false);
        solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);

        assertThrows(SolicitudCerradaException.class,
                () -> solicitud.asignar(new Responsable("Laura Gomez", Area.ALUMNOS)));
    }
}
