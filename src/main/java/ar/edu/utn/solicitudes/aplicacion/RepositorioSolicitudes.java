package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia. El servicio depende de esta interfaz y no de un archivo concreto
 * (inversión de dependencias): en producción la implementa {@code RepositorioCsv}; en las pruebas,
 * una versión en memoria.
 */
public interface RepositorioSolicitudes {

    List<Solicitud> todas();

    Optional<Solicitud> buscarPorId(int id);

    Optional<Estudiante> estudiante(String legajo);

    int siguienteId();

    /** Agrega una solicitud nueva y, si no existía, su estudiante. */
    void agregar(Solicitud solicitud, Estudiante estudiante);

    /** Persiste los cambios de una solicitud que ya estaba guardada. */
    void actualizar(Solicitud solicitud);
}
