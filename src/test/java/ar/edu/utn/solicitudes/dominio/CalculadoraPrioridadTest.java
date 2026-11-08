package ar.edu.utn.solicitudes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalculadoraPrioridadTest {

    private final CalculadoraPrioridad calculadora = new CalculadoraPrioridad();

    @Test
    @DisplayName("Una solicitud urgente entra con prioridad alta aunque su tipo sea de prioridad baja")
    void urgenteSiempreEsAlta() {
        // Arrange
        TipoSolicitud tipo = TipoSolicitud.CONSTANCIA;

        // Act
        Prioridad prioridad = calculadora.calcular(tipo, "Constancia de alumno regular", true);

        // Assert
        assertEquals(Prioridad.ALTA, prioridad);
    }

    @Test
    @DisplayName("Sin urgencia, cada tipo entra con su prioridad base")
    void sinUrgenciaUsaLaPrioridadDelTipo() {
        assertEquals(Prioridad.BAJA, calculadora.calcular(TipoSolicitud.CONSTANCIA, "texto", false));
        assertEquals(Prioridad.ALTA, calculadora.calcular(TipoSolicitud.EXAMEN, "texto", false));
        assertEquals(Prioridad.MEDIA, calculadora.calcular(TipoSolicitud.EQUIVALENCIA, "texto", false));
    }

    @Test
    @DisplayName("Valor límite: un reclamo de exactamente 200 caracteres sigue en prioridad media")
    void reclamoEnElLimite() {
        String descripcion = "x".repeat(CalculadoraPrioridad.LARGO_RECLAMO_EXTENSO);

        assertEquals(Prioridad.MEDIA, calculadora.calcular(TipoSolicitud.RECLAMO, descripcion, false));
    }

    @Test
    @DisplayName("Valor límite: un reclamo de 201 caracteres pasa a prioridad alta")
    void reclamoQueSuperaElLimite() {
        String descripcion = "x".repeat(CalculadoraPrioridad.LARGO_RECLAMO_EXTENSO + 1);

        assertEquals(Prioridad.ALTA, calculadora.calcular(TipoSolicitud.RECLAMO, descripcion, false));
    }
}
