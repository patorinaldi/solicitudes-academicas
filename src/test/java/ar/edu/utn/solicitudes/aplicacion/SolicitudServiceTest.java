package ar.edu.utn.solicitudes.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.solicitudes.dominio.Area;
import ar.edu.utn.solicitudes.dominio.CalculadoraPrioridad;
import ar.edu.utn.solicitudes.dominio.EstadoSolicitud;
import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Responsable;
import ar.edu.utn.solicitudes.dominio.ResponsableInexistenteException;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.SolicitudInexistenteException;
import ar.edu.utn.solicitudes.dominio.TransicionInvalidaException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del servicio aislado de la consola y del archivo: el repositorio es un doble en memoria
 * y el directorio de responsables un stub con un solo responsable.
 */
class SolicitudServiceTest {

    private RepositorioEnMemoria repositorio;
    private SolicitudService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        DirectorioResponsables stub = nombre -> nombre.equalsIgnoreCase("Martin Paz")
                ? Optional.of(new Responsable("Martin Paz", Area.BEDELIA))
                : Optional.empty();
        servicio = new SolicitudService(repositorio, stub, new ValidadorSolicitud(), new CalculadoraPrioridad());
    }

    private static NuevaSolicitud examen(String legajo, String nombre) {
        return new NuevaSolicitud(legajo, nombre, "alumno@example.edu", "TUP", " examen ",
                "  Mesa especial  ", "10/03/2026", false);
    }

    @Test
    @DisplayName("Registra con id consecutivo, prioridad calculada y datos normalizados")
    void registrar() {
        // Act
        Solicitud primera = servicio.registrar(examen("10002", "Bruno Diaz"));
        Solicitud segunda = servicio.registrar(examen("10003", "Carla Mendez"));

        // Assert
        assertEquals(1, primera.id());
        assertEquals(2, segunda.id());
        assertEquals(Prioridad.ALTA, primera.prioridad());
        assertEquals("Mesa especial", primera.descripcion());
        assertEquals(EstadoSolicitud.PENDIENTE, primera.estado());
    }

    @Test
    @DisplayName("Si el legajo ya existe se conservan los datos guardados del estudiante")
    void estudianteExistente() {
        servicio.registrar(examen("10002", "Bruno Diaz"));

        servicio.registrar(examen("10002", "Otro Nombre"));

        assertEquals("Bruno Diaz", servicio.estudiante("10002").nombre());
    }

    @Test
    @DisplayName("Asignar responsable persiste el cambio una sola vez")
    void asignarPersiste() {
        Solicitud solicitud = servicio.registrar(examen("10002", "Bruno Diaz"));

        servicio.asignarResponsable(solicitud.id(), " martin paz ");

        assertEquals(Optional.of("Martin Paz"), solicitud.responsable());
        assertEquals(1, repositorio.actualizaciones);
    }

    @Test
    @DisplayName("Una operación que falla no persiste nada")
    void errorNoPersiste() {
        Solicitud solicitud = servicio.registrar(examen("10002", "Bruno Diaz"));

        assertThrows(ResponsableInexistenteException.class,
                () -> servicio.asignarResponsable(solicitud.id(), "Nadie"));
        assertThrows(TransicionInvalidaException.class, () -> servicio.cambiarEstado(solicitud.id(), "CERRADA"));
        assertThrows(SolicitudInexistenteException.class, () -> servicio.cambiarEstado(99, "EN_PROCESO"));
        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    @DisplayName("Busca con el filtro y arma el reporte sobre lo guardado")
    void buscarYReportar() {
        servicio.registrar(examen("10002", "Bruno Diaz"));
        servicio.registrar(new NuevaSolicitud("10001", "Ana Torres", "ana@example.edu", "TUP", "CONSTANCIA",
                "Constancia", "03/03/2026", false));

        assertEquals(1, servicio.buscar(new FiltroSolicitudes("EXAMEN", "", "", "")).size());
        assertEquals(2, servicio.reporte().total());
    }
}
