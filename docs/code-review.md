# Revisión de código

| | |
|---|---|
| **Alcance** | Todos los cambios entre `estado-inicial` y `etapa-pruebas` |
| **Checklist** | `docs/code-review-checklist.md` |
| **Autores** | Patricio Tomas Rinaldi y Elian Rivoira (refactoring y correcciones) |
| **Revisor** | Juan Francisco Reales (Issue de revisión en GitHub) |
| **Herramientas** | lizard y jscpd (`scripts/metricas.sh`), suite de pruebas, lectura del diff |

## 1. Resultado del checklist

| Sección | Resultado | Observaciones |
|---|---|---|
| 1. Lógica y funcionalidad | Cumple | Golden master en verde en todos los commits; los cambios de comportamiento (D1, D2) están aislados y justificados en ADR-003 |
| 2. Legibilidad | Cumple | Nombres del dominio, métodos cortos y sin literales mágicos |
| 3. Diseño | **No cumple** | CR-01, CR-02, CR-03 |
| 4. Manejo de errores | Cumple, con una sugerencia | CR-04 |
| 5. Seguridad y datos | Cumple | Datos sintéticos; D2 corregido |
| 6. Rendimiento | Cumple | El reporte recorre la lista una sola vez (antes, seis veces) |
| 7. Pruebas | Cumple | 39 pruebas; el servicio se prueba con dobles, sin disco |
| 8. Documentación | **No cumple** | CR-05 y CR-06 |

## 2. Observaciones

Severidad: **Bloqueante** (impide aprobar) o **Sugerencia** (mejora recomendada).

| ID | Ubicación (`etapa-pruebas`) | Observación | Severidad | Acción | Resultado |
|---|---|---|---|---|---|
| CR-01 | `Solicitud` (constructor) y `Solicitud.nueva` | El constructor volvió a tener **9 parámetros** y `nueva`, 7: es el mismo smell (S3) que el refactoring resolvió en `registrar()`. lizard lo marca | Bloqueante | Introduce Parameter Object: record `DetalleSolicitud` | Corregido en `afc7d38`: 5 y 3 parámetros |
| CR-02 | `ValidadorSolicitud.validar` | Complejidad ciclomática **12**, el único método por encima de 10 | Bloqueante (TAC-4) | Extract Method por campo | Corregido en `519f439`: CCN máximo del proyecto, 9 |
| CR-03 | `TipoSolicitud.desde` y `EstadoSolicitud.desde` | El mismo bucle copiado en los dos enums. jscpd no lo detecta porque es corto, pero es duplicación (DRY) | Sugerencia | Extraer `TextoEnum.buscar()` | Corregido en `2ef8811` |
| CR-04 | `SolicitudService.estudiante` | `orElseThrow()` sin mensaje: si falta el estudiante, el error no dice qué pasó | Sugerencia | Excepción con mensaje | Corregido en `f1b6075` |
| CR-05 | `RepositorioCsv.actualizar` | No usa su parámetro, y no queda claro por qué | Sugerencia | Documentar la decisión (reescribe el archivo completo) | Corregido en `f1b6075` |
| CR-06 | `README.md` | Sigue describiendo la versión inicial: no menciona la arquitectura, las pruebas ni las métricas | Bloqueante (TAC-8) | Actualizar README y agregar los ADR | Corregido en la etapa de documentación |
| CR-07 | `MenuConsola.cambiarEstado` | Atrapa cualquier `SolicitudException` y muestra siempre el mismo mensaje, sin la causa | Sugerencia, **no aplicada** | Se mantiene a propósito: es el mensaje de la versión inicial y cambiarlo rompería el golden master. Queda como mejora futura | Pendiente, registrada en las conclusiones |

## 3. Feedback al autor

> El refactoring está muy bien trazado: un commit por técnica, cada uno con el golden master en
> verde, y las dos correcciones separadas del cambio de estructura, con su justificación. Eso hace
> que la revisión se pueda seguir paso a paso, y es lo que más vale de este PR.
>
> Hay dos puntos que bloquean la aprobación porque reintroducen problemas que el mismo trabajo
> se propuso eliminar. El constructor de `Solicitud` quedó con 9 parámetros, el mismo smell que se
> resolvió en `registrar()`, y `ValidadorSolicitud.validar` supera el umbral de complejidad que
> fijamos como criterio de aceptación (TAC-4). Las dos correcciones son mecánicas, con las mismas
> técnicas que ya se aplicaron (Introduce Parameter Object y Extract Method).
>
> Las otras observaciones son menores y se pueden resolver en el mismo PR. La de `MenuConsola`
> (CR-07) la dejaría como está, porque cambiar el mensaje alteraría el comportamiento externo.
> Con estas correcciones apruebo.

## 4. Criterios de aceptación después de las correcciones

| Criterio | Antes de la revisión | Después |
|---|---|---|
| TAC-1 Compila | Cumple | Cumple |
| TAC-2 100% de pruebas en verde | Cumple (39/39) | Cumple (39/39) |
| TAC-3 Golden master | Cumple | Cumple |
| TAC-4 CCN ≤ 10 y ≤ 5 parámetros | **No cumple** (CCN 12; 9 parámetros) | Cumple (CCN máx. 9; 5 parámetros) |
| TAC-5 Sin duplicación | Cumple según jscpd (con CR-03 pendiente) | Cumple |
| TAC-6 Sin `catch` vacíos ni códigos de error | Cumple | Cumple |
| TAC-7 Reglas con prueba | Cumple | Cumple |
| TAC-8 Documentación | **No cumple** (README desactualizado) | Cumple |

**Estado: aprobado** después de las correcciones.
