# Gestión de Solicitudes Académicas

Trabajo Práctico Integrador de **Metodología de Sistemas II** (UTN, Tecnicatura Universitaria en
Programación a Distancia). Aplicación de consola para registrar y seguir las solicitudes que los
estudiantes presentan ante la facultad: constancias, mesas de examen, equivalencias y reclamos.

El foco del trabajo no es la funcionalidad sino la **calidad interna**: el repositorio contiene una
versión inicial con problemas reales de diseño, su diagnóstico, el refactoring paso a paso, las
pruebas que demuestran que el comportamiento externo no cambió y la revisión de código.

## Funcionalidades

| Operación | Detalle |
|---|---|
| Registrar una solicitud | Valida legajo, nombre, email, tipo, descripción y fecha; calcula la prioridad y el área |
| Cambiar el estado | PENDIENTE → EN_PROCESO o RECHAZADA; EN_PROCESO → RESUELTA o RECHAZADA. Una urgente no se puede rechazar |
| Asignar un responsable | Solo del área que corresponde al tipo; una solicitud pendiente pasa a en proceso |
| Buscar | Por tipo, estado, prioridad y legajo, combinables |
| Reporte | Totales por estado y prioridad, urgentes y abiertas sin responsable |
| Listar todas | |

Los datos se guardan en un archivo CSV separado por `;`. Todos los datos de ejemplo son sintéticos.

## Instalación y uso

Requiere Java 21 y Maven 3.9.

```bash
mvn verify                                                  # compila, corre las 39 pruebas y genera la cobertura
java -jar target/solicitudes-academicas-1.0.0.jar           # usa solicitudes.csv
java -jar target/solicitudes-academicas-1.0.0.jar datos.csv # otro archivo
```

El informe de cobertura queda en `target/site/jacoco/index.html`. Las métricas de complejidad y
duplicación se regeneran con `scripts/metricas.sh` (necesita `pip install lizard` y Node.js).

## Tecnologías

Java 21, Maven, JUnit 5, JaCoCo; lizard y jscpd para las métricas.

## Arquitectura

```
ar.edu.utn.solicitudes
├── Main                 arma las piezas y arranca el menú
├── consola              MenuConsola: entrada y salida
├── aplicacion           SolicitudService (casos de uso), ValidadorSolicitud,
│                        puertos RepositorioSolicitudes y DirectorioResponsables
├── dominio              Solicitud, DetalleSolicitud, enums, reglas y excepciones
└── persistencia         RepositorioCsv, ResponsablesFijos
```

Las dependencias apuntan hacia el dominio y el servicio recibe interfaces por constructor
(`docs/decisiones/ADR-001`).

## Recorrido del trabajo

| Tag | Etapa | Qué mirar |
|---|---|---|
| `estado-inicial` | Sistema base | `GestorSolicitudes` (clase gigante) y `Main.main` (135 líneas) |
| `etapa-diagnostico` | Diagnóstico | `docs/diagnostico.md`: 13 smells, 2 defectos, métricas; `GoldenMasterTest` |
| `etapa-refactoring` | Refactoring | 7 commits `refactor:`, uno por técnica, cada uno con el golden master en verde |
| `etapa-pruebas` | Pruebas | `docs/pruebas.md`: 18 casos, 39 pruebas, correcciones D1 y D2, antes/después |
| `etapa-docs` | Revisión y documentación | `docs/code-review.md`, checklist, ADR, uso de IA |
| `estado-refactorizado` | Versión final | El código de `src/` |

Para comparar las dos versiones: `git diff estado-inicial estado-refactorizado -- src/main`.

| Métrica | Inicial | Final |
|---|---|---|
| Método más complejo (CCN) | 32 | 9 |
| Métodos con CCN > 10 | 6 de 14 | 0 de 80 |
| Duplicación | 3 clones | 0 |
| Pruebas | 0 | 39 |

## Documentación

- `docs/diagnostico.md`: calidad externa e interna, deuda técnica y code smells.
- `docs/pruebas.md`: estrategia, casos de prueba y comparación antes/después.
- `docs/code-review-checklist.md` y `docs/code-review.md`: checklist, observaciones y criterios de aceptación.
- `docs/decisiones/`: decisiones técnicas (ADR).
- `docs/evidencias/`: salidas de las herramientas antes y después.
- `docs/uso-ia.md`: declaración del uso de IA.

## Integrantes

| Integrante | Participación |
|---|---|
| Patricio Tomas Rinaldi | Versión inicial; refactoring 1 (Extract Method, Introduce Parameter Object, enums); corrección D1; correcciones CR-01 y CR-02 |
| Elian Rivoira | Diagnóstico; refactoring 2 (Move Method, excepciones, encapsulamiento, capas); corrección D2; correcciones CR-03 a CR-05 |
| Juan Francisco Reales | Golden master; pruebas unitarias; evidencias; revisión de código formal; documentación final |
