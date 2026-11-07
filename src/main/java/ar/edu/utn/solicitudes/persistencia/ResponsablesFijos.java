package ar.edu.utn.solicitudes.persistencia;

import ar.edu.utn.solicitudes.aplicacion.DirectorioResponsables;
import ar.edu.utn.solicitudes.dominio.Area;
import ar.edu.utn.solicitudes.dominio.Responsable;
import java.util.List;
import java.util.Optional;

/** Responsables de cada área. Hoy son fijos; mañana podrían salir de un archivo o de una base. */
public class ResponsablesFijos implements DirectorioResponsables {

    private static final List<Responsable> RESPONSABLES = List.of(
            new Responsable("Laura Gomez", Area.ALUMNOS),
            new Responsable("Martin Paz", Area.BEDELIA),
            new Responsable("Sofia Ruiz", Area.SECRETARIA_ACADEMICA),
            new Responsable("Diego Lopez", Area.ALUMNOS));

    @Override
    public Optional<Responsable> buscarPorNombre(String nombre) {
        return RESPONSABLES.stream().filter(r -> r.nombre().equalsIgnoreCase(nombre)).findFirst();
    }
}
