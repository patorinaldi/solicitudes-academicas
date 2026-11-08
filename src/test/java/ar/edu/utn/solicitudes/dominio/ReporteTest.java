package ar.edu.utn.solicitudes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReporteTest {

    @Test
    @DisplayName("Cuenta por estado, por prioridad, las urgentes y las abiertas sin responsable")
    void cuentaCadaCriterio() {
        // Arrange
        Solicitud pendiente = Solicitud.nueva(1, "10001", TipoSolicitud.CONSTANCIA, "a", "01/03/2026",
                Prioridad.BAJA, false);
        Solicitud urgente = Solicitud.nueva(2, "10002", TipoSolicitud.RECLAMO, "b", "02/03/2026",
                Prioridad.ALTA, true);
        Solicitud resuelta = Solicitud.nueva(3, "10003", TipoSolicitud.EXAMEN, "c", "03/03/2026",
                Prioridad.ALTA, false);
        resuelta.asignar(new Responsable("Martin Paz", Area.BEDELIA));
        resuelta.cambiarEstado(EstadoSolicitud.RESUELTA);

        // Act
        Reporte reporte = Reporte.de(List.of(pendiente, urgente, resuelta));

        // Assert
        assertEquals(3, reporte.total());
        assertEquals(2, (int) reporte.porEstado().get(EstadoSolicitud.PENDIENTE));
        assertEquals(1, (int) reporte.porEstado().get(EstadoSolicitud.RESUELTA));
        assertEquals(0, (int) reporte.porEstado().get(EstadoSolicitud.RECHAZADA));
        assertEquals(2, (int) reporte.porPrioridad().get(Prioridad.ALTA));
        assertEquals(1, reporte.urgentes());
        assertEquals(2, reporte.abiertasSinResponsable());
    }

    @Test
    @DisplayName("El texto del reporte respeta el formato de la versión inicial")
    void formatoDelTexto() {
        Reporte vacio = Reporte.de(List.of());

        assertEquals("""
                === REPORTE ===
                Total: 0
                PENDIENTE: 0
                EN_PROCESO: 0
                RESUELTA: 0
                RECHAZADA: 0
                Prioridad ALTA: 0 | MEDIA: 0 | BAJA: 0
                Urgentes: 0
                Abiertas sin responsable: 0
                """, vacio.comoTexto());
    }
}
