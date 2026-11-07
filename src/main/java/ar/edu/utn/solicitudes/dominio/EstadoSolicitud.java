package ar.edu.utn.solicitudes.dominio;

import java.util.Optional;
import java.util.Set;

/** Estados de una solicitud y las transiciones que admite cada uno. */
public enum EstadoSolicitud {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    RECHAZADA;

    public boolean puedePasarA(EstadoSolicitud nuevo) {
        return switch (this) {
            case PENDIENTE -> Set.of(EN_PROCESO, RECHAZADA).contains(nuevo);
            case EN_PROCESO -> Set.of(RESUELTA, RECHAZADA).contains(nuevo);
            case RESUELTA, RECHAZADA -> false;
        };
    }

    public boolean estaCerrado() {
        return this == RESUELTA || this == RECHAZADA;
    }

    public static Optional<EstadoSolicitud> desde(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String normalizado = texto.trim().toUpperCase();
        for (EstadoSolicitud estado : values()) {
            if (estado.name().equals(normalizado)) {
                return Optional.of(estado);
            }
        }
        return Optional.empty();
    }
}
