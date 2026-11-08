package ar.edu.utn.solicitudes;

import ar.edu.utn.solicitudes.aplicacion.PersistenciaException;
import ar.edu.utn.solicitudes.aplicacion.SolicitudService;
import ar.edu.utn.solicitudes.aplicacion.ValidadorSolicitud;
import ar.edu.utn.solicitudes.consola.MenuConsola;
import ar.edu.utn.solicitudes.dominio.CalculadoraPrioridad;
import ar.edu.utn.solicitudes.persistencia.RepositorioCsv;
import ar.edu.utn.solicitudes.persistencia.ResponsablesFijos;
import java.nio.file.Path;

/** Punto de entrada: arma las piezas y arranca el menú. */
public class Main {

    private static final String ARCHIVO_POR_DEFECTO = "solicitudes.csv";

    public static void main(String[] args) {
        Path archivo = Path.of(args.length > 0 ? args[0] : ARCHIVO_POR_DEFECTO);
        RepositorioCsv repositorio;
        try {
            repositorio = new RepositorioCsv(archivo);
        } catch (PersistenciaException e) {
            // No se arranca con datos a medias: guardar encima borraría las filas que no se pudieron leer.
            System.out.println("Error: " + e.getMessage());
            return;
        }
        SolicitudService servicio = new SolicitudService(repositorio, new ResponsablesFijos(),
                new ValidadorSolicitud(), new CalculadoraPrioridad());
        new MenuConsola(servicio, System.in, System.out).ejecutar();
    }
}
