# Uso de inteligencia artificial

La consigna permite usar IA como apoyo y pide declarar la herramienta, la finalidad y el grado de
intervención, y adjuntar la evidencia del intercambio.

| Aspecto | Detalle |
|---|---|
| **Herramienta** | Claude (Anthropic), en la aplicación web, con cuentas personales de los integrantes |
| **Finalidad** | Consultas conceptuales, interpretación de métricas, comparación de alternativas de diseño, casos de prueba de borde, revisión del checklist y de la redacción |
| **Grado de intervención** | **Moderado.** La IA propone y explica; el equipo decide, escribe y verifica |
| **Qué no hizo la IA** | No escribió el código del sistema, no aplicó los refactorings, no redactó secciones del informe ni hizo la revisión de código |
| **Cómo se verificó** | Cada sugerencia aceptada se contrastó con el material de la cátedra o se comprobó corriendo el código, las pruebas o las métricas |
| **Datos compartidos** | Solo fragmentos del propio código y salidas de herramientas. Ningún dato personal: el sistema usa datos sintéticos |

## Límites y verificación
- Toda salida de la IA se verificó ejecutándola: el golden master y las 39 pruebas, y las métricas
  con lizard y jscpd, cuyas salidas completas están en `docs/evidencias/`.
- La IA no tuvo acceso a datos personales: todos los datos del sistema son sintéticos.
