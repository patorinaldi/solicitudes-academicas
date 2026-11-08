package ar.edu.utn.solicitudes.dominio;

import java.util.Optional;

/** Cada tipo sabe a qué área corresponde y con qué prioridad entra. */
public enum TipoSolicitud {
    CONSTANCIA(Area.ALUMNOS, Prioridad.BAJA),
    EXAMEN(Area.BEDELIA, Prioridad.ALTA),
    EQUIVALENCIA(Area.SECRETARIA_ACADEMICA, Prioridad.MEDIA),
    RECLAMO(Area.ALUMNOS, Prioridad.MEDIA);

    private final Area area;
    private final Prioridad prioridadBase;

    TipoSolicitud(Area area, Prioridad prioridadBase) {
        this.area = area;
        this.prioridadBase = prioridadBase;
    }

    public Area area() {
        return area;
    }

    public Prioridad prioridadBase() {
        return prioridadBase;
    }

    /** Interpreta lo que escribe el usuario, sin distinguir mayúsculas ni espacios. */
    public static Optional<TipoSolicitud> desde(String texto) {
        return TextoEnum.buscar(TipoSolicitud.class, texto);
    }
}
