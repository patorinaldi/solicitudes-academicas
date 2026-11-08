package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.DetalleSolicitud;
import ar.edu.utn.solicitudes.dominio.CalculadoraPrioridad;
import ar.edu.utn.solicitudes.dominio.EstadoSolicitud;
import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Prioridad;
import ar.edu.utn.solicitudes.dominio.Reporte;
import ar.edu.utn.solicitudes.dominio.ResponsableInexistenteException;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.SolicitudInexistenteException;
import ar.edu.utn.solicitudes.dominio.TipoSolicitud;
import ar.edu.utn.solicitudes.dominio.TransicionInvalidaException;
import java.util.List;

/**
 * Casos de uso del sistema. Coordina: valida la entrada, delega las reglas en el dominio y
 * persiste a través del repositorio. No sabe nada de la consola ni del archivo.
 */
public class SolicitudService {

    private final RepositorioSolicitudes repositorio;
    private final DirectorioResponsables responsables;
    private final ValidadorSolicitud validador;
    private final CalculadoraPrioridad calculadoraPrioridad;

    public SolicitudService(RepositorioSolicitudes repositorio, DirectorioResponsables responsables,
                            ValidadorSolicitud validador, CalculadoraPrioridad calculadoraPrioridad) {
        this.repositorio = repositorio;
        this.responsables = responsables;
        this.validador = validador;
        this.calculadoraPrioridad = calculadoraPrioridad;
    }

    /** Registra una solicitud. Si el estudiante ya existe, se conservan sus datos guardados. */
    public Solicitud registrar(NuevaSolicitud datos) {
        validador.validar(datos);
        TipoSolicitud tipo = TipoSolicitud.desde(datos.tipo()).orElseThrow();
        Estudiante estudiante = repositorio.estudiante(datos.legajo().trim())
                .orElseGet(() -> estudianteNuevo(datos));
        String descripcion = datos.descripcion().trim();
        Prioridad prioridad = calculadoraPrioridad.calcular(tipo, descripcion, datos.urgente());
        DetalleSolicitud detalle = new DetalleSolicitud(tipo, descripcion,
                ValidadorSolicitud.fecha(datos).orElseThrow(), prioridad, datos.urgente());
        Solicitud solicitud = Solicitud.nueva(repositorio.siguienteId(), estudiante.legajo(), detalle);
        repositorio.agregar(solicitud, estudiante);
        return solicitud;
    }

    public void cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = buscar(id);
        EstadoSolicitud nuevo = EstadoSolicitud.desde(nuevoEstado)
                .orElseThrow(() -> new TransicionInvalidaException("estado desconocido: " + nuevoEstado));
        solicitud.cambiarEstado(nuevo);
        repositorio.actualizar(solicitud);
    }

    public void asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = buscar(id);
        String nombre = nombreResponsable == null ? "" : nombreResponsable.trim();
        solicitud.asignar(responsables.buscarPorNombre(nombre).orElseThrow(ResponsableInexistenteException::new));
        repositorio.actualizar(solicitud);
    }

    public List<Solicitud> buscar(FiltroSolicitudes filtro) {
        return repositorio.todas().stream().filter(filtro::admite).toList();
    }

    public List<Solicitud> todas() {
        return repositorio.todas();
    }

    public Estudiante estudiante(String legajo) {
        return repositorio.estudiante(legajo).orElseThrow();
    }

    public Reporte reporte() {
        return Reporte.de(repositorio.todas());
    }

    private Solicitud buscar(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() -> new SolicitudInexistenteException(id));
    }

    private static Estudiante estudianteNuevo(NuevaSolicitud datos) {
        String carrera = datos.carrera() == null ? "" : datos.carrera().trim();
        return new Estudiante(datos.legajo().trim(), datos.nombre().trim(), datos.email().trim(), carrera);
    }
}
