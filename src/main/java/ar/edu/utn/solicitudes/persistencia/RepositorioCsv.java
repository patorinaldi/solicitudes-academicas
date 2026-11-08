package ar.edu.utn.solicitudes.persistencia;

import ar.edu.utn.solicitudes.aplicacion.RepositorioSolicitudes;
import ar.edu.utn.solicitudes.dominio.EstadoSolicitud;
import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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

    @Override
    public void actualizar(Solicitud solicitud) {
        guardar();
    }

    private void guardar() {
        try {
            FileWriter fw = new FileWriter(archivo.toFile());
            fw.write(ENCABEZADO + "\n");
            for (Solicitud solicitud : solicitudes) {
                fw.write(aLinea(solicitud) + "\n");
            }
            fw.close();
        } catch (Exception ex) {
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
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo.toFile()));
            String linea = br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";", -1);
                estudiantes.putIfAbsent(campos[LEGAJO], new Estudiante(campos[LEGAJO], campos[NOMBRE],
                        campos[EMAIL], campos[CARRERA]));
                solicitudes.add(new Solicitud(Integer.parseInt(campos[ID]), campos[LEGAJO],
                        TipoSolicitud.valueOf(campos[TIPO]), campos[DESCRIPCION],
                        LocalDate.parse(campos[FECHA], Solicitud.FORMATO_FECHA),
                        Prioridad.valueOf(campos[PRIORIDAD]), campos[URGENTE].equals("true"),
                        EstadoSolicitud.valueOf(campos[ESTADO]), campos[RESPONSABLE]));
            }
            br.close();
        } catch (Exception ex) {
        }
    }
}
