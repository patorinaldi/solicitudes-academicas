package ar.edu.utn.solicitudes;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Prueba de caracterización (golden master).
 *
 * <p>Fija el comportamiento externo de la versión inicial: lo que la consola muestra y lo que queda
 * en el archivo de datos. Las salidas esperadas se grabaron corriendo la versión del tag
 * {@code estado-inicial} con las mismas entradas. Mientras esta prueba pase, ningún refactoring
 * cambió lo que el usuario ve.
 *
 * <p>La sesión 1 recorre las seis operaciones, incluidos los errores de validación, las
 * transiciones de estado inválidas y la solicitud urgente que no se puede rechazar. La sesión 2
 * arranca desde el archivo que dejó la sesión 1, para probar también la carga de datos.
 */
class GoldenMasterTest {

    @Test
    @DisplayName("Sesión 1: misma salida de consola y mismo archivo que la versión inicial")
    void sesionCompletaDesdeCero() throws IOException {
        Path archivo = Files.createTempDirectory("golden").resolve("datos.csv");

        String salida = ejecutar(archivo, "golden/sesion1.txt");

        assertEquals(recurso("golden/salida1.txt"), salida);
        assertEquals(recurso("golden/datos-1.csv"), Files.readString(archivo, UTF_8));
    }

    @Test
    @DisplayName("Sesión 2: carga el archivo existente y continúa con los mismos resultados")
    void sesionQueRetomaUnArchivoExistente() throws IOException {
        Path archivo = Files.createTempDirectory("golden").resolve("datos.csv");
        Files.writeString(archivo, recurso("golden/datos-1.csv"), UTF_8);

        String salida = ejecutar(archivo, "golden/sesion2.txt");

        assertEquals(recurso("golden/salida2.txt"), salida);
        assertEquals(recurso("golden/datos-2.csv"), Files.readString(archivo, UTF_8));
    }

    private static String ejecutar(Path archivo, String entrada) {
        InputStream entradaOriginal = System.in;
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(recurso(entrada).getBytes(UTF_8)));
            System.setOut(new PrintStream(capturada, true, UTF_8));
            Main.main(new String[] {archivo.toString()});
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(salidaOriginal);
        }
        // En Windows println usa \r\n; las salidas grabadas usan \n.
        return capturada.toString(UTF_8).replace("\r\n", "\n");
    }

    private static String recurso(String nombre) {
        try (InputStream in = GoldenMasterTest.class.getClassLoader().getResourceAsStream(nombre)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el recurso " + nombre);
            }
            return new String(in.readAllBytes(), UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
