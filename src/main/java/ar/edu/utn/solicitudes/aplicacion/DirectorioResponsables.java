package ar.edu.utn.solicitudes.aplicacion;

import ar.edu.utn.solicitudes.dominio.Responsable;
import java.util.Optional;

/** De dónde salen los responsables de cada área. */
public interface DirectorioResponsables {

    /** Busca por nombre, sin distinguir mayúsculas. */
    Optional<Responsable> buscarPorNombre(String nombre);
}
