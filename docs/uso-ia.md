# Uso de inteligencia artificial

La consigna permite usar IA como apoyo y pide declarar la herramienta, la finalidad y el grado de
intervención, y adjuntar la evidencia del intercambio.

| | |
|---|---|
| **Herramienta** | Claude (Anthropic), en la aplicación de Claude |
| **Finalidad** | Construir el prototipo inicial, proponer y aplicar los refactorings, escribir las pruebas, medir, redactar la documentación y el borrador del informe |
| **Grado de intervención** | **Alto.** La IA escribió la mayor parte del código y de los documentos a partir de las decisiones y los pedidos del equipo |
| **Qué hizo el equipo** | Eligió el caso, la tecnología y la forma de trabajo; revisó cada etapa; ejecutó el proyecto y las pruebas; hizo la revisión de código; grabó el video y es responsable de entender y defender todo lo entregado |
| **Evidencia** | La conversación completa, exportada, va en el anexo ZIP |

## Registro de prompts

| # | Etapa | Prompt (resumen fiel) | Resultado |
|---|---|---|---|
| 1 | Preparación | Procesar el material teórico de la materia (calidad de código, patrones, V&V, Git, CI/CD, habilidades blandas, IA) para empezar el TPI | Resumen del material disponible |
| 2 | Preparación | Se comparte la consigna del TPI, la página de la cátedra y el modelo de presentación | Preguntas sobre caso, integrantes, tecnología y repositorio |
| 3 | Decisiones | Respuestas: prototipo nuevo de Gestión de Solicitudes Académicas; grupo del TFI; Java 21 + Maven + JUnit; repositorio armado localmente para subirlo | Plan por etapas con los tags de la consigna |
| 4 | Etapas 1 a 7 | Construcción de la versión inicial, diagnóstico, refactoring, pruebas, revisión y documentación | Este repositorio |

_Completar con los prompts que el equipo use después de esta entrega (por ejemplo, para ajustar el
informe o preparar el video)._

## Límites y verificación
- Toda salida de la IA se verificó ejecutándola: el golden master y las 39 pruebas, y las métricas
  con lizard y jscpd, cuyas salidas completas están en `docs/evidencias/`.
- La IA no tuvo acceso a datos personales: todos los datos del sistema son sintéticos.
