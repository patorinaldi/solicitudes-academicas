# ADR-002: Golden master como red de seguridad del refactoring

**Estado:** Aceptado. **Commit:** `adfccec`.

## Contexto
La versión inicial no tenía pruebas (S13) y su diseño no permitía escribir pruebas unitarias: la
lógica estaba mezclada con la consola y el archivo. Refactorizar así no tenía forma de verificarse.

## Decisión
Antes de cambiar una línea, se graba el comportamiento externo de la versión inicial: dos sesiones
de consola que recorren las seis operaciones y sus errores, con la salida en pantalla y el archivo
que queda. `GoldenMasterTest` compara contra esa grabación y se corre en cada commit.

## Consecuencias
- Cada refactoring se verifica contra el comportamiento real, no contra lo que se cree que hace.
- La prueba detecta cualquier cambio de texto, incluso uno deseado: un cambio de comportamiento
  intencional obliga a regrabar las salidas y a justificarlo (ADR-003).
- No reemplaza a las pruebas unitarias: dice que algo cambió, pero no dónde. Por eso se agregaron
  después del refactoring, cuando las reglas quedaron aisladas.
