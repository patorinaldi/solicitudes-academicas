package ar.edu.utn.solicitudes;

/** Regla de negocio de la prioridad con la que entra una solicitud. */
public class CalculadoraPrioridad {

    /** Un reclamo con una descripción más larga que esto se considera complejo. */
    static final int LARGO_RECLAMO_EXTENSO = 200;

    public Prioridad calcular(TipoSolicitud tipo, String descripcion, boolean urgente) {
        if (urgente) {
            return Prioridad.ALTA;
        }
        if (tipo == TipoSolicitud.RECLAMO && descripcion.length() > LARGO_RECLAMO_EXTENSO) {
            return Prioridad.ALTA;
        }
        return tipo.prioridadBase();
    }
}
