package ar.edu.utn.solicitudes.persistencia;

import ar.edu.utn.solicitudes.aplicacion.RepositorioSolicitudes;
import ar.edu.utn.solicitudes.dominio.DetalleSolicitud;
import ar.edu.utn.solicitudes.dominio.EstadoSolicitud;
import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import ar.edu.utn.solicitudes.aplicacion.PersistenciaException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Guarda las solicitudes en un archivo CSV separado por {@code ;}, con una fila por solicitud y los
 * datos del estudiante repetidos en cada fila. El formato es el mismo de la versión inicial.
 */
public class RepositorioCsv implements RepositorioSolicitudes {

    static final String ENCABEZADO =
            "id;legajo;nombre;email;carrera;tipo;descripcion;fecha;estado;prioridad;responsable;urgente";

    private static final int ID = 0;
    private static final int LEGAJO = 1;
    private static final int NOMBRE = 2;
    private static final int EMAIL = 3;
    private static final int CARRERA = 4;
    private static final int TIPO = 5;
    private static final int DESCRIPCION = 6;
    private static final int FECHA = 7;
    private static final int ESTADO = 8;
    private static final int PRIORIDAD = 9;
    private static final int RESPONSABLE = 10;
    private static final int URGENTE = 11;
    private static final int CANTIDAD_DE_CAMPOS = 12;

    private final Path archivo;
    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final Map<String, Estudiante> estudiantes = new HashMap<>();

    public RepositorioCsv(Path archivo) {
        this.archivo = archivo;
        cargar();
    }

    @Override
    public List<Solicitud> todas() {
        return List.copyOf(solicitudes);
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return solicitudes.stream().filter(s -> s.id() == id).findFirst();
    }

    @Override
    public Optional<Estudiante> estudiante(String legajo) {
        return Optional.ofNullable(estudiantes.get(legajo));
    }

    @Override
    public int siguienteId() {
        return solicitudes.stream().mapToInt(Solicitud::id).max().orElse(0) + 1;
    }

    @Override
    public void agregar(Solicitud solicitud, Estudiante estudiante) {
        estudiantes.putIfAbsent(estudiante.legajo(), estudiante);
        solicitudes.add(solicitud);
        guardar();
    }

    /**
     * La solicitud ya está en memoria con sus cambios; alcanza con reescribir el archivo completo.
     * Es la misma estrategia de la versión inicial y es suficiente para el volumen de este sistema.
     */
    @Override
    public void actualizar(Solicitud solicitud) {
        guardar();
    }

    private void guardar() {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo)) {
            escritor.write(ENCABEZADO + "\n");
            for (Solicitud solicitud : solicitudes) {
                escritor.write(aLinea(solicitud) + "\n");
            }
        } catch (IOException e) {
            throw new PersistenciaException("no se pudo guardar el archivo " + archivo, e);
        }
    }

    private String aLinea(Solicitud s) {
        Estudiante e = estudiantes.get(s.legajo());
        return String.join(";", String.valueOf(s.id()), s.legajo(), e.nombre(), e.email(), e.carrera(),
                s.tipo().name(), s.descripcion(), s.fecha().format(Solicitud.FORMATO_FECHA), s.estado().name(),
                s.prioridad().name(),
                s.responsable().orElse(""), String.valueOf(s.esUrgente()));
    }

    private void cargar() {
        if (!Files.exists(archivo)) {
            return;
        }
        List<String> lineas;
        try {
            lineas = Files.readAllLines(archivo);
        } catch (IOException e) {
            throw new PersistenciaException("no se pudo leer el archivo " + archivo, e);
        }
        for (int numero = 2; numero <= lineas.size(); numero++) {
            try {
                cargarLinea(lineas.get(numero - 1));
            } catch (RuntimeException e) {
                throw new PersistenciaException("la linea " + numero + " del archivo " + archivo + " no es valida", e);
            }
        }
    }

    private void cargarLinea(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length != CANTIDAD_DE_CAMPOS) {
            throw new IllegalArgumentException("se esperaban " + CANTIDAD_DE_CAMPOS + " campos y hay " + campos.length);
        }
        estudiantes.putIfAbsent(campos[LEGAJO],
                new Estudiante(campos[LEGAJO], campos[NOMBRE], campos[EMAIL], campos[CARRERA]));
        DetalleSolicitud detalle = new DetalleSolicitud(TipoSolicitud.valueOf(campos[TIPO]), campos[DESCRIPCION],
                LocalDate.parse(campos[FECHA], Solicitud.FORMATO_FECHA), Prioridad.valueOf(campos[PRIORIDAD]),
                Boolean.parseBoolean(campos[URGENTE]));
        solicitudes.add(new Solicitud(Integer.parseInt(campos[ID]), campos[LEGAJO], detalle,
                EstadoSolicitud.valueOf(campos[ESTADO]), campos[RESPONSABLE]));
    }
}
