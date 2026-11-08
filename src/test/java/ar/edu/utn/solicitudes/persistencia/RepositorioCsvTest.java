package ar.edu.utn.solicitudes.persistencia;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.solicitudes.aplicacion.PersistenciaException;
import ar.edu.utn.solicitudes.dominio.DetalleSolicitud;
import ar.edu.utn.solicitudes.dominio.Area;
import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Responsable;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Prueba de integración con un archivo real en un directorio temporal. */
class RepositorioCsvTest {

    private Path archivo;

    @BeforeEach
    void preparar() throws IOException {
        archivo = Files.createTempDirectory("csv").resolve("datos.csv");
    }

    private static Solicitud examen(int id) {
        return Solicitud.nueva(id, "10002", new DetalleSolicitud(TipoSolicitud.EXAMEN, "Mesa especial",
                LocalDate.of(2026, 3, 10), Prioridad.ALTA, false));
    }

    @Test
    @DisplayName("Guarda con el formato de la versión inicial y lo vuelve a leer igual")
    void idaYVuelta() throws IOException {
        // Arrange
        RepositorioCsv repositorio = new RepositorioCsv(archivo);
        Solicitud solicitud = examen(1);
        repositorio.agregar(solicitud, new Estudiante("10002", "Bruno Diaz", "bruno@example.edu", "TUP"));
        solicitud.asignar(new Responsable("Martin Paz", Area.BEDELIA));
        repositorio.actualizar(solicitud);

        // Act
        RepositorioCsv releido = new RepositorioCsv(archivo);

        // Assert
        assertEquals(List.of(RepositorioCsv.ENCABEZADO,
                        "1;10002;Bruno Diaz;bruno@example.edu;TUP;EXAMEN;Mesa especial;10/03/2026;EN_PROCESO;ALTA;"
                                + "Martin Paz;false"),
                Files.readAllLines(archivo, UTF_8));
        Solicitud leida = releido.buscarPorId(1).orElseThrow();
        assertEquals(Optional.of("Martin Paz"), leida.responsable());
        assertEquals(LocalDate.of(2026, 3, 10), leida.fecha());
        assertEquals(2, releido.siguienteId());
    }

    @Test
    @DisplayName("Corrección D2: una línea corrupta se informa en lugar de ignorarse en silencio")
    void lineaCorrupta() throws IOException {
        Files.writeString(archivo, RepositorioCsv.ENCABEZADO + "\n"
                + "2;10008;Hugo Ruiz;hugo@example.edu;TUP;RECLAMO;Nota de Algebra; no figura;05/04/2026;PENDIENTE;"
                + "MEDIA;;false\n", UTF_8);

        PersistenciaException error = assertThrows(PersistenciaException.class, () -> new RepositorioCsv(archivo));

        assertTrue(error.getMessage().startsWith("la linea 2 del archivo"), error.getMessage());
    }

    @Test
    @DisplayName("Si no se puede escribir, el error se informa en lugar de perderse")
    void archivoNoEscribible() throws IOException {
        Path enCarpetaInexistente = Files.createTempDirectory("csv").resolve("no-existe").resolve("datos.csv");
        RepositorioCsv repositorio = new RepositorioCsv(enCarpetaInexistente);

        assertThrows(PersistenciaException.class, () -> repositorio.agregar(examen(1),
                new Estudiante("10002", "Bruno Diaz", "bruno@example.edu", "TUP")));
    }
}
