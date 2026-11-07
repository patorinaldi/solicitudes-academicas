package ar.edu.utn.solicitudes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Totales de un conjunto de solicitudes. Se calcula una vez y se presenta como texto. */
public record Reporte(int total, Map<EstadoSolicitud, Integer> porEstado, Map<Prioridad, Integer> porPrioridad,
                      int urgentes, int abiertasSinResponsable) {

    public static Reporte de(List<Solicitud> solicitudes) {
        Map<EstadoSolicitud, Integer> porEstado = new EnumMap<>(EstadoSolicitud.class);
        Map<Prioridad, Integer> porPrioridad = new EnumMap<>(Prioridad.class);
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            porEstado.put(estado, 0);
        }
        for (Prioridad prioridad : Prioridad.values()) {
            porPrioridad.put(prioridad, 0);
        }
        int urgentes = 0;
        int abiertasSinResponsable = 0;
        for (Solicitud solicitud : solicitudes) {
            porEstado.merge(solicitud.estado, 1, Integer::sum);
            porPrioridad.merge(solicitud.prioridad, 1, Integer::sum);
            if (solicitud instanceof SolicitudUrgente) {
                urgentes++;
            }
            if (solicitud.responsable.isEmpty() && !solicitud.estado.estaCerrado()) {
                abiertasSinResponsable++;
            }
        }
        return new Reporte(solicitudes.size(), porEstado, porPrioridad, urgentes, abiertasSinResponsable);
    }

    public String comoTexto() {
        StringBuilder texto = new StringBuilder("=== REPORTE ===\n");
        texto.append("Total: ").append(total).append('\n');
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            texto.append(estado).append(": ").append(porEstado.get(estado)).append('\n');
        }
        texto.append("Prioridad ALTA: ").append(porPrioridad.get(Prioridad.ALTA))
                .append(" | MEDIA: ").append(porPrioridad.get(Prioridad.MEDIA))
                .append(" | BAJA: ").append(porPrioridad.get(Prioridad.BAJA)).append('\n');
        texto.append("Urgentes: ").append(urgentes).append('\n');
        texto.append("Abiertas sin responsable: ").append(abiertasSinResponsable).append('\n');
        return texto.toString();
    }
}
