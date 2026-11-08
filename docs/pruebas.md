# Pruebas y verificación

## Estrategia

| Capa | Qué protege | Cuándo se escribió |
|---|---|---|
| **Golden master** (`GoldenMasterTest`) | El comportamiento externo completo: salida de consola y archivo CSV en dos sesiones | Antes de refactorizar (`etapa-diagnostico`) |
| **Pruebas unitarias** (patrón AAA) | Cada regla del dominio y del servicio por separado, incluidos los valores límite | Después del refactoring, cuando las reglas quedaron aisladas |
| **Prueba de integración** (`RepositorioCsvTest`) | El formato del archivo y los errores de lectura y escritura | Con la corrección D2 |

El golden master es la evidencia central de que el refactoring no alteró el comportamiento: las
salidas esperadas se grabaron corriendo la versión `estado-inicial` y la prueba se ejecutó sobre
**cada** commit posterior (`docs/evidencias/golden-master-por-commit.txt`). Antes de usarlo se
comprobó que detecta cambios: al alterar a propósito la regla de prioridad de las urgentes, la
prueba falla.

## Casos de prueba

| ID | Caso | Entrada | Resultado esperado | Prueba |
|---|---|---|---|---|
| CP01 | Registro válido de cada tipo | Las cuatro solicitudes de la sesión 1 | Id consecutivo, prioridad y área correctas | Golden master, `SolicitudServiceTest.registrar` |
| CP02 | Legajo inválido | `1234` | `Error: el legajo debe tener 5 digitos` | Golden master, `ValidadorSolicitudTest.legajo` |
| CP03 | Email inválido | `eva.example.edu`, `eva@example` | `Error: email invalido` | Golden master, `ValidadorSolicitudTest.email` |
| CP04 | Descripción en el límite | 300 y 301 caracteres | Acepta 300, rechaza 301 | `ValidadorSolicitudTest.largoDeLaDescripcion` |
| CP05 | Prioridad de un reclamo en el límite | 200 y 201 caracteres | MEDIA y ALTA | `CalculadoraPrioridadTest` |
| CP06 | Estudiante ya registrado | Legajo existente con otro nombre | Se conservan los datos guardados | Golden master, `SolicitudServiceTest.estudianteExistente` |
| CP07 | Transición inválida | PENDIENTE → RESUELTA; RESUELTA → RECHAZADA | `No se pudo cambiar el estado` y el estado no cambia | Golden master, `SolicitudTest.cambioDeEstadoInvalido` |
| CP08 | Urgente rechazada | Solicitud urgente → RECHAZADA | Se rechaza el cambio | Golden master, `SolicitudTest.urgenteNoSePuedeRechazar` |
| CP09 | Responsable de otra área | Laura Gomez (ALUMNOS) a un EXAMEN | `Error: el responsable no pertenece al area de la solicitud` | Golden master, `SolicitudTest.responsableDeOtraArea` |
| CP10 | Asignar a una solicitud cerrada | Solicitud resuelta | `Error: la solicitud esta cerrada` | Golden master, `SolicitudTest.solicitudCerrada` |
| CP11 | Búsqueda con filtros | Por tipo, estado, prioridad, legajo y combinados | Las solicitudes que coinciden | Golden master, `FiltroSolicitudesTest` |
| CP12 | Reporte | Cinco solicitudes en distintos estados | Totales por estado, prioridad, urgentes y sin responsable | Golden master, `ReporteTest` |
| CP13 | Carga de un archivo existente | Archivo de la sesión 1 | Continúa con el id 6 | Golden master (sesión 2), `RepositorioCsvTest.idaYVuelta` |
| CP14 | Una operación que falla no persiste | Responsable o estado inexistente | Cero actualizaciones del repositorio | `SolicitudServiceTest.errorNoPersiste` (doble que audita la interacción) |
| CP15 | **D1** Fecha inexistente | `31/02/2026` | `Error: la fecha 31/02/2026 no existe`; `29/02/2028` se acepta | `ValidadorSolicitudTest.fechaInexistente` |
| CP16 | **D2** Separador en la descripción | Texto con `;` | `Error: la descripcion no puede contener ';'` | `ValidadorSolicitudTest.descripcionConSeparador` |
| CP17 | **D2** Archivo corrupto | Línea con un campo de más | Se informa la línea; el sistema no arranca con datos a medias | `RepositorioCsvTest.lineaCorrupta` |
| CP18 | Archivo no escribible | Carpeta inexistente | `PersistenciaException` en lugar de un error silenciado | `RepositorioCsvTest.archivoNoEscribible` |

## Resultados

- **39 pruebas, 39 en verde** (`docs/evidencias/final/pruebas.txt`). `mvn verify` además deja el
  informe de cobertura de JaCoCo en `target/site/jacoco/index.html`.
- **Golden master en verde en los 12 commits** desde que existe, incluidos los 7 de refactoring y
  los 2 de corrección.

## Antes y después

| Métrica | `estado-inicial` | `estado-refactorizado` |
|---|---|---|
| Método más complejo (CCN) | 32 (`Main.main`) | 9 (`MenuConsola.atender`, el `switch` del menú) |
| Métodos con CCN > 10 | 6 de 14 | 0 de 80 |
| CCN promedio por método | 9,7 | 1,9 |
| Líneas del método más largo | 135 (`Main.main`) | 18 |
| Máximo de parámetros | 8 (`registrar`) | 5 (constructor de `Solicitud`) |
| Duplicación (jscpd) | 3 clones, 25 líneas (4,71%) | 0 clones |
| Literales de dominio repetidos | 48 | 0 (enums) |
| `catch` vacíos | 2 | 0 |
| Archivos de código | 6 | 29 |
| Pruebas automáticas | 0 | 39 |
| Defectos conocidos | 2 (D1, D2) | 0 |

La cantidad de líneas crece (de 490 a 766) porque cada responsabilidad pasó a tener su clase, su
nombre y su documentación; lo que baja es la complejidad que hay que entender para tocar cada parte.
Las salidas completas de `lizard` y `jscpd` están en
`docs/evidencias/inicial/` y `docs/evidencias/final/`.
