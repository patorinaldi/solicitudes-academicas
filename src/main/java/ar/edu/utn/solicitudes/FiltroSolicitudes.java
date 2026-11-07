package ar.edu.utn.solicitudes;

/** Criterios de búsqueda. Un criterio vacío o nulo no filtra. */
public record FiltroSolicitudes(String tipo, String estado, String prioridad, String legajo) {

    public boolean admite(Solicitud solicitud) {
        return coincide(tipo, solicitud.tipo().name(), true)
                && coincide(estado, solicitud.estado().name(), true)
                && coincide(prioridad, solicitud.prioridad().name(), true)
                && coincide(legajo, solicitud.legajo(), false);
    }

    private static boolean coincide(String criterio, String valor, boolean ignorarMayusculas) {
        if (criterio == null || criterio.isBlank()) {
            return true;
        }
        String buscado = ignorarMayusculas ? criterio.trim().toUpperCase() : criterio.trim();
        return valor.equals(buscado);
    }
}
