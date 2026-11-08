# ADR-003: Corregir los defectos después del refactoring, en commits separados

**Estado:** Aceptado. **Commits:** `c8ee571` (D1) y `9d684eb` (D2).

## Contexto
El diagnóstico encontró dos defectos visibles: el sistema aceptaba fechas inexistentes (D1) y un
`;` en la descripción corrompía el archivo sin aviso (D2). Corregirlos cambia el comportamiento
externo, y un refactoring, por definición, no lo cambia.

## Decisión
Los siete commits de refactoring conservan el comportamiento exacto de la versión inicial, incluidos
los dos defectos. Las correcciones van después, en commits `fix:` propios, cada una con sus pruebas.

## Consecuencias
- El golden master verifica que el refactoring es puro, y las pruebas nuevas verifican las
  correcciones. Mezclarlos habría hecho imposible saber si una diferencia era un error o una mejora.
- Un archivo guardado por la versión inicial con una fecha inexistente o con la descripción corrida
  ya no se carga: el sistema informa la línea y no arranca. Es deliberado (guardar encima borraría
  las filas que no se leyeron), pero ese archivo hay que corregirlo a mano.
