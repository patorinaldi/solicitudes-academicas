package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Doble de prueba del repositorio: guarda en memoria y registra cuántas veces se le pidió
 * persistir, para poder verificar la interacción sin escribir archivos.
 */
class RepositorioEnMemoria implements RepositorioSolicitudes {

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final Map<String, Estudiante> estudiantes = new HashMap<>();
    int actualizaciones;

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
        return solicitudes.size() + 1;
    }

    @Override
    public void agregar(Solicitud solicitud, Estudiante estudiante) {
        estudiantes.putIfAbsent(estudiante.legajo(), estudiante);
        solicitudes.add(solicitud);
    }

    @Override
    public void actualizar(Solicitud solicitud) {
        actualizaciones++;
    }
}
