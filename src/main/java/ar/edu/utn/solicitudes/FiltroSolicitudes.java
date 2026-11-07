package ar.edu.utn.solicitudes;

/** Criterios de búsqueda. Un criterio vacío o nulo no filtra. */
public record FiltroSolicitudes(String tipo, String estado, String prioridad, String legajo) {
}
