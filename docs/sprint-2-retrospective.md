# Sprint 2 Retrospective — AgroValle Connect

**Fecha:** 4 de noviembre de 2026 · **Participantes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias. Redactada en tercera persona.

## Qué salió bien

- El equipo separó el checkout en `CheckoutStrategy`, `WompiPseCheckoutStrategy` y `PagoPseService`, lo que permitió probar cada capa de forma aislada con Mockito (53 pruebas).
- El pipeline `ci.yml` con servicio PostgreSQL ejecutó Checkstyle, pruebas y JaCoCo y terminó en verde sobre `main`.

## Qué se debe mejorar

- Se hicieron commits directos a `main` que debieron revertirse mediante el PR #27 y reenviarse por el PR #29; la integración debe hacerse siempre por Pull Request.
- El PR #29 se integró sin revisión de un par, por lo que la aprobación obligatoria debe activarse en Branch Protection.
- El tablero mantuvo muchas tarjetas en Backlog y pocas en Hecho; debe actualizarse a diario y mostrar sus límites WIP.
- Las pruebas locales usan H2 y la CI usa PostgreSQL; la cobertura solo se da por válida con el run de CI.

## Acciones acordadas

| Acción | Responsable | Fecha límite | Evidencia de cierre |
|---|---|---|---|
| Activar Branch Protection en `main` con 1 aprobación y status check `build-and-test`. | Miguel Ángel Hurtado Arias | 4/11/2026 | Captura de la regla activa |
| Declarar los límites WIP en las columnas y actualizar las tarjetas. | Juan David Chicangana Rodríguez | 4/11/2026 | Captura del tablero |
| Confirmar con el Product Owner el catálogo de municipios de HU-01. | Juan José Guerrero Ospina | Sprint 3 |
| Documentar e implementar el patrón Observer para HU-14. | Juan David Chicangana Rodríguez | Sprint 3 | Decisión versionada |

## Medición de flujo

- **WIP máximo:** En curso ≤ 3; En revisión ≤ 2.
- **Aprendizaje principal:** integrar solo por Pull Request revisado por un par y con la CI en verde.
