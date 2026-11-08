# Checklist de revisión de código

Se aplica a cada Pull Request antes de aprobarlo. Cada punto se marca como **Cumple**, **No cumple**
(genera una observación) o **No aplica**.

## 1. Lógica y funcionalidad
- [ ] El cambio hace lo que dice el PR y el Issue asociado.
- [ ] Cubre los casos de borde: nulos, vacíos, límites (300 caracteres, 5 dígitos, 200 caracteres de reclamo).
- [ ] No cambia el comportamiento externo sin decirlo (el golden master sigue en verde o el PR explica por qué no).

## 2. Legibilidad (Clean Code)
- [ ] Nombres del dominio y descriptivos; sin variables de una letra fuera de bucles triviales.
- [ ] Métodos cortos, de un solo nivel de abstracción (referencia: menos de 30 líneas).
- [ ] Sin números ni cadenas mágicos: constantes con nombre o enums.
- [ ] Los comentarios explican el porqué, no repiten el qué.

## 3. Diseño (SOLID y DRY)
- [ ] **SRP:** cada clase tiene una sola razón para cambiar.
- [ ] **OCP:** agregar un tipo, un estado o un área no obliga a modificar cadenas de `if`.
- [ ] **LSP:** ninguna subclase restringe lo que promete su padre.
- [ ] **ISP:** las interfaces exponen solo lo que sus clientes usan.
- [ ] **DIP:** la lógica depende de interfaces (`RepositorioSolicitudes`, `DirectorioResponsables`), no de archivos ni de la consola.
- [ ] **DRY:** sin lógica duplicada (jscpd sin clones).
- [ ] Complejidad ciclomática de cada método ≤ 10 (lizard).
- [ ] Ningún método con más de 5 parámetros.
- [ ] Cada clase está en el paquete de su capa (`dominio`, `aplicacion`, `persistencia`, `consola`) y las dependencias apuntan hacia el dominio.

## 4. Manejo de errores
- [ ] Sin `catch` vacíos ni errores silenciados.
- [ ] Errores de negocio como excepciones con mensaje claro, no como códigos de retorno.
- [ ] Los recursos (archivos) se cierran siempre (`try-with-resources`).

## 5. Seguridad y datos
- [ ] Sin datos personales reales: solo datos sintéticos.
- [ ] Una entrada del usuario no puede corromper el archivo de datos.

## 6. Rendimiento
- [ ] Sin trabajo repetido innecesario (por ejemplo, recorrer la lista una vez por cada contador).

## 7. Pruebas
- [ ] Toda regla nueva o modificada tiene su prueba, con el patrón AAA.
- [ ] Las pruebas unitarias no tocan el disco: usan dobles (`RepositorioEnMemoria`, stubs).
- [ ] `mvn verify` en verde.

## 8. Documentación y trazabilidad
- [ ] README y `docs/` actualizados si cambia el uso, el formato o una decisión.
- [ ] Una decisión de arquitectura nueva tiene su ADR en `docs/decisiones/`.
- [ ] Commits atómicos con prefijo (`feat`, `fix`, `refactor`, `test`, `docs`, `review`) y mensaje que explica el porqué.

## Criterios de aceptación técnica

Un PR se aprueba solo si cumple **todos** estos mínimos:

| # | Criterio | Cómo se verifica |
|---|---|---|
| TAC-1 | Compila sin errores | `mvn verify` |
| TAC-2 | El 100% de las pruebas pasa | `mvn verify` |
| TAC-3 | El golden master pasa, o el cambio de comportamiento está justificado en un ADR | `GoldenMasterTest` |
| TAC-4 | Ningún método supera CCN 10 ni 5 parámetros | `scripts/metricas.sh` (lizard) |
| TAC-5 | Sin código duplicado | `scripts/metricas.sh` (jscpd) |
| TAC-6 | Sin `catch` vacíos ni códigos de error | Revisión manual |
| TAC-7 | Las reglas nuevas tienen prueba | Revisión manual |
| TAC-8 | Documentación actualizada | Revisión manual |
