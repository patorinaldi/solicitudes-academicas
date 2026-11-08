# ADR-001: Separar el sistema en capas que dependen del dominio

**Estado:** Aceptado. **Commit:** `20e83af`.

## Contexto
En la versión inicial, `GestorSolicitudes` validaba, aplicaba las reglas, escribía el archivo CSV
y además imprimía en la consola (smells S1 y S10). No se podía probar una regla sin crear un
archivo real, y cambiar el formato de guardado obligaba a tocar la lógica de negocio.

## Decisión
Cuatro paquetes, con las dependencias apuntando hacia el dominio:

- `dominio`: entidades, enums y reglas. No depende de nada.
- `aplicacion`: casos de uso (`SolicitudService`) y las interfaces que necesita
  (`RepositorioSolicitudes`, `DirectorioResponsables`).
- `persistencia`: las implementaciones de esas interfaces (`RepositorioCsv`, `ResponsablesFijos`).
- `consola`: la interfaz con el usuario (`MenuConsola`).

`Main` arma las piezas e inyecta las dependencias por constructor.

## Consecuencias
- El servicio se prueba con dobles, sin disco (`SolicitudServiceTest`).
- Cambiar el CSV por una base de datos es escribir otra implementación de `RepositorioSolicitudes`.
- Más archivos (de 6 a 29) y una indirección más para seguir el flujo.
