package ar.edu.utn.solicitudes.dominio;

import java.util.Arrays;
import java.util.Optional;

/** Convierte lo que escribe el usuario en un valor de un enum, sin distinguir mayúsculas ni espacios. */
final class TextoEnum {

    private TextoEnum() {
    }

    static <E extends Enum<E>> Optional<E> buscar(Class<E> tipo, String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String normalizado = texto.trim().toUpperCase();
        return Arrays.stream(tipo.getEnumConstants()).filter(valor -> valor.name().equals(normalizado)).findFirst();
    }
}
