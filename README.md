# Gestión de Solicitudes Académicas

Aplicación de consola para registrar y seguir las solicitudes que los estudiantes presentan
ante la facultad: constancias, mesas de examen, equivalencias y reclamos.

## Funcionalidades

- Registrar una solicitud (con los datos del estudiante).
- Cambiar el estado de una solicitud.
- Asignar un responsable del área que corresponde.
- Buscar solicitudes por tipo, estado, prioridad o legajo.
- Ver un reporte con totales.

## Uso

Requiere Java 21 y Maven.

```bash
mvn package
java -jar target/solicitudes-academicas-1.0.0.jar            # usa solicitudes.csv
java -jar target/solicitudes-academicas-1.0.0.jar datos.csv  # otro archivo
```

Los datos se guardan en un archivo CSV separado por `;`.
