# Diagnóstico de calidad de la versión inicial

Versión analizada: tag `estado-inicial`. Las líneas citadas corresponden a esa versión.

## Cómo se hizo

1. **Red de seguridad primero.** Antes de tocar el código se agregó `GoldenMasterTest`, una
   prueba de caracterización que graba la salida de consola y el archivo CSV de dos sesiones
   completas. Pasa sobre la versión inicial y tiene que seguir pasando después de cada refactoring.
2. **Métricas.** Complejidad ciclomática (CCN), largo y cantidad de parámetros por método con
   `lizard`; duplicación con `jscpd`. Las salidas están en `docs/evidencias/inicial/` y se
   regeneran con `scripts/metricas.sh`.
3. **Revisión manual** del código contra el catálogo de code smells y los principios SOLID.
4. **Pruebas exploratorias** para buscar defectos visibles: `docs/evidencias/inicial/defectos.txt`.

## Métricas de la versión inicial

| Métrica | Valor |
|---|---|
| Líneas de código (NLOC) | 490 |
| Métodos | 14 |
| Métodos con CCN > 10 | 6 de 14 |
| Método más complejo | `Main.main`: CCN 32, 135 líneas |
| Segundo más complejo | `GestorSolicitudes.registrar`: CCN 28, 80 líneas, 8 parámetros |
| Duplicación (jscpd) | 3 clones, 25 líneas (4,71%) |
| Literales de dominio repetidos | 48 apariciones de 14 cadenas (`"PENDIENTE"`, `"EXAMEN"`, `"ALUMNOS"`, …) |
| Pruebas automáticas en `estado-inicial` | 0 |

## Calidad externa: dos defectos visibles

El sistema cumple las seis operaciones, pero tiene dos defectos que el usuario puede encontrar:

- **D1. Acepta fechas inexistentes.** La fecha se valida con una expresión regular
  (`GestorSolicitudes.java:48`), así que `31/02/2026` se registra sin error.
- **D2. Un `;` en la descripción corrompe el archivo sin aviso.** El CSV usa `;` como separador y
  no se escapa (`:287-289`). Al volver a abrir el sistema los campos quedan corridos y la
  solicitud deja de poder cambiar de estado. El `catch` vacío de `cargar()` (`:334`) oculta el
  problema.

Los dos se corrigen **después** de terminar el refactoring, en un commit aparte (ver
`docs/decisiones/ADR-003`), para no mezclar cambios de estructura con cambios de comportamiento.

## Code smells y debilidades técnicas

Prioridad: **Alta** = bloquea el refactoring o produce defectos; **Media** = encarece cada
cambio; **Baja** = afecta la legibilidad.

| # | Smell | Ubicación (`estado-inicial`) | Evidencia | Impacto | Riesgo de mantenimiento | Prioridad |
|---|---|---|---|---|---|---|
| S1 | **Clase gigante** (God Class) | `GestorSolicitudes` (337 líneas) | Valida, calcula prioridad y área, cambia estados, persiste en CSV, arma el reporte y escribe en consola (`:106`) | Cualquier cambio toca la misma clase; no se puede probar una regla sin el resto | Muy alto: viola SRP; todo cambio arriesga romper algo no relacionado | Alta |
| S2 | **Método largo** | `GestorSolicitudes.registrar` (`:29-108`) y `Main.main` (`:8-142`) | CCN 28 y 32; 80 y 135 líneas | Difíciles de leer y de probar por partes | Alto: cada regla nueva agranda el método | Alta |
| S3 | **Lista larga de parámetros** | `registrar(...)` (`:29`, 8 parámetros, 7 `String` seguidos) y `buscar(...)` (`:185`, 4 `String` opcionales) | Lizard: 8 y 4 parámetros | Es fácil pasar el email en el lugar del nombre: el compilador no lo detecta | Medio: cada dato nuevo cambia la firma y todos los llamadores | Alta |
| S4 | **Código duplicado** | Búsqueda por id (`:112-119` y `:144-151`); área por tipo (`:98-104` y `:162-168`); impresión de una solicitud en `Main` (`:115-123` y `:128-136`); cuatro bucles de conteo en `reporte()` | jscpd: 3 clones, 25 líneas | Una regla vive en dos lugares | Alto: cambiar el área de un tipo obliga a acordarse de las dos copias | Alta |
| S5 | **Obsesión por primitivos** | `tipo`, `estado`, `prioridad`, `area` y `fecha` como `String` en `Solicitud` (`:7-12`) y `Responsable` | 48 literales repetidos; la fecha se valida con regex | Valores inválidos posibles en cualquier punto; origen del defecto D1 | Alto: un error de tipeo en un literal compila igual | Alta |
| S6 | **Cadenas de `if` sobre el tipo** (viola OCP) | Prioridad (`:77-94`), área (`:98-104` y `:162-168`), transiciones de estado (`:122-132`) | Tres cadenas de decisión sobre el mismo dato | Agregar un tipo obliga a modificar código que ya funciona | Medio: el cambio se reparte en 4 lugares | Media |
| S7 | **Códigos de error y errores silenciados** | `registrar` devuelve `-1..-6`, `asignarResponsable` `0..4`, `cambiarEstado` `boolean`; `catch` vacíos en `guardar()` (`:292`) y `cargar()` (`:334`); `Main` traduce los códigos con `if` (`:52-63`, `:93-102`) | `return -3`, `catch (Exception ex) { }` | El llamador tiene que conocer el significado de cada número; un fallo de disco pasa inadvertido | Alto: origen del defecto D2 | Alta |
| S8 | **Clase de datos con campos públicos** | `Solicitud` (`:5-12`), `Estudiante`, `Responsable` | Todos los campos `public`; `asignarResponsable` escribe `s.estado` directamente (`:178`) | Cualquier clase puede dejar una solicitud en un estado inválido salteando las reglas | Alto: la regla de la solicitud urgente se puede saltear sin querer | Media |
| S9 | **Violación de LSP** | `SolicitudUrgente.cambiarEstado` (`:7-12`) | Lanza `UnsupportedOperationException`; el gestor compensa con `instanceof` (`:133`, `:269`, `:289`) | Una `SolicitudUrgente` no puede usarse donde se espera una `Solicitud` | Medio: cada cliente nuevo tiene que conocer la subclase | Media |
| S10 | **Dependencia de detalles** (viola DIP) | `new FileWriter(archivo)` (`:283`), `System.out` en la lógica (`:106`), responsables fijos en el constructor (`:20-23`) | El gestor crea su propia persistencia | No se puede probar sin escribir un archivo real; cambiar el formato toca la lógica | Alto | Alta |
| S11 | **Números y cadenas mágicos** | `300`, `200` (`:45`, `:86`); índices `p[0]..p[11]` (15 usos en `cargar()`) | Literales sin nombre | El significado de cada número hay que deducirlo | Medio | Media |
| S12 | **Nombres poco expresivos** | `g`, `sc`, `op`, `r`, `t`, `n`, `s`, `e`, `c`, `a`, `m`, `b`, `u`, `sin`, `aux`, `lst`, `p` | Variables de una letra en métodos de 50 a 135 líneas | Hay que leer todo el método para saber qué guarda cada variable | Bajo | Baja |
| S13 | **Sin pruebas automáticas** | `estado-inicial` no tiene `src/test` | 0 pruebas | No hay forma de saber si un cambio rompe algo | Muy alto: impide refactorizar con seguridad | Alta (resuelto primero con `GoldenMasterTest`) |

## Orden de corrección

1. S13: red de seguridad (ya hecha, antes del refactoring).
2. S2, S3, S4: Extract Method e Introduce Parameter Object, que preparan el resto.
3. S1, S10: separar responsabilidades y depender de interfaces.
4. S5, S6, S9, S11: modelar el dominio con tipos propios.
5. S7, S8: excepciones en lugar de códigos y encapsulamiento.
6. S12: nombres, en cada paso.
7. D1 y D2: corrección del comportamiento, al final y aparte.
