package ar.edu.utn.solicitudes;

/**
 * Datos que ingresa el usuario para registrar una solicitud, tal como llegan de la consola.
 * Agrupa los ocho valores que antes viajaban como parámetros sueltos de registrar().
 */
public record NuevaSolicitud(String legajo, String nombre, String email, String carrera, String tipo,
                             String descripcion, String fecha, boolean urgente) {
}
