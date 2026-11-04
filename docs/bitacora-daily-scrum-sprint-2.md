# Bitácora Daily Scrum — Sprint 2

**Proyecto:** AgroValle Connect · **Sprint 2:** 15 de octubre al 4 de noviembre de 2026 · **Formato:** tercera persona.
**Equipo:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias.
**Meta del Sprint 2:** consolidar los artefactos de diseño, la separación por capas, el checkout extensible (Strategy) y la suite de calidad (pruebas, JaCoCo y CI) para el segundo corte.

## Reunión #4 — 15 de octubre de 2026, Microsoft Teams (Sprint 2: día 1)

**Asistentes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias.

| Integrante | Qué realizó desde la reunión anterior | Qué realizará a continuación | Impedimentos |
|---|---|---|---|
| Andrés Camilo Fuelagan Cuastumal | Verificó el código del Sprint 1 (HU-01) y dejó listos los servicios para la refactorización por capas. | Diseñar la interfaz `CheckoutStrategy` y separar los servicios del flujo de pago. | Ninguno |
| Juan David Chicangana Rodríguez | Revisó la especificación de la HU-01 bajo ISO/IEC/IEEE 29148 frente a los escenarios BDD. | Completar la matriz comparativa IEEE 29148 vs BDD y verificar HU-06. | Ninguno |
| Juan José Guerrero Ospina | Elaboró el modelo de datos (MER, DER en 3FN y `schema.sql` para PostgreSQL). | Preparar los wireframes y mockups de las pantallas principales. | Ninguno |
| Miguel Ángel Hurtado Arias | Publicó los diagramas UML (despliegue, secuencia y componentes) el 14/10. | Configurar el workflow `ci.yml` y el plugin de JaCoCo. | Ninguno |

**WIP observado:** En curso 2 / límite 3; En revisión 0 / límite 2.
**Acuerdos:** Iniciar el Sprint 2 con HU-02 y HU-04, y construir el pipeline de CI en paralelo al refactor.
**Evidencia:** Commit `Diagramas UML` (14/10); tablero Kanban.

## Reunión #5 — 22 de octubre de 2026, Microsoft Teams (Sprint 2: seguimiento)

**Asistentes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias.

| Integrante | Qué realizó desde la reunión anterior | Qué realizará a continuación | Impedimentos |
|---|---|---|---|
| Andrés Camilo Fuelagan Cuastumal | Integró HU-02 (publicación de productos) y comenzó HU-09 (pago PSE con Wompi). | Terminar la integración de PSE y escribir pruebas con Mockito. | Ninguno |
| Juan David Chicangana Rodríguez | Cerró la reescritura de la HU-01 en `docs/ieee29148-req01.md`. | Documentar el estado de las historias y preparar la bitácora del sprint. | Ninguno |
| Juan José Guerrero Ospina | Implementó el filtro de categorías (HU-04) y avanzó con los wireframes del cliente. | Completar los mockups de administración y de cliente. | Ninguno |
| Miguel Ángel Hurtado Arias | Creó `ci.yml` con Checkstyle, pruebas y JaCoCo, usando un servicio PostgreSQL en el runner. | Ejecutar el pipeline sobre `main` y revisar el reporte de cobertura. | Las pruebas necesitaban una base PostgreSQL en la CI; se resolvió con el servicio del workflow. |

**WIP observado:** En curso 3 / límite 3; En revisión 1 / límite 2.
**Acuerdos:** Mantener los límites WIP y abrir Pull Requests con revisión de un par.
**Evidencia:** Workflow `.github/workflows/ci.yml`; tablero Kanban.

## Reunión #6 — 29 de octubre de 2026, Microsoft Teams (Sprint 2: seguimiento)

**Asistentes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias.

| Integrante | Qué realizó desde la reunión anterior | Qué realizará a continuación | Impedimentos |
|---|---|---|---|
| Andrés Camilo Fuelagan Cuastumal | Avanzó en HU-09 y en la separación de servicios con la estrategia de checkout. | Abrir el Pull Request de la refactorización y de las pruebas unitarias. | Ninguno |
| Juan David Chicangana Rodríguez | Redactó la propuesta del patrón Observer para HU-14 y empezó la documentación del sprint. | Completar la bitácora, el review y la retrospectiva. | Ninguno |
| Juan José Guerrero Ospina | Subió los wireframes y mockups definitivos de administración y de cliente (28/10). | Reunir las evidencias para el informe. | Ninguno |
| Miguel Ángel Hurtado Arias | Implementó HU-13 (historial de compras) y ajustó el tablero. | Revisar la configuración de Branch Protection en `main`. | Ninguno |

**WIP observado:** En curso 3 / límite 3; En revisión 2 / límite 2.
**Acuerdos:** Cerrar la refactorización SOLID y Strategy mediante Pull Request.
**Evidencia:** Wireframes y mockups en `docs/diseño/`; tablero Kanban.

## Reunión #7 — 4 de noviembre de 2026, Microsoft Teams (Sprint 2: cierre)

**Asistentes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias.

| Integrante | Qué realizó desde la reunión anterior | Qué realizará a continuación | Impedimentos |
|---|---|---|---|
| Andrés Camilo Fuelagan Cuastumal | Integró el PR #29 (refactor SOLID, Strategy y pruebas unitarias) en `main`. | Apoyar la verificación de la CI y de las pruebas. | Se hicieron commits directos a `main` que debieron revertirse (PR #27) y reenviarse por Pull Request. |
| Juan David Chicangana Rodríguez | Completó la documentación del Sprint 2 y la retrospectiva. | Enviar el PR de documentación a revisión. | Ninguno |
| Juan José Guerrero Ospina | Reunió las capturas de CI, JaCoCo y tablero y armó el informe PDF. | Revisar el informe antes de cargarlo. | Ninguno |
| Miguel Ángel Hurtado Arias | Verificó el run en verde del último commit de `main` y configuró Branch Protection. | Comprobar la regla con un Pull Request aprobado por un par. | El PR #29 se integró sin revisión de un par; se activa la aprobación obligatoria. |

**WIP observado:** En curso 3 / límite 3; En revisión 2 / límite 2.
**Acuerdos:** Integrar solo por Pull Request con aprobación de un par; declarar los límites WIP en las columnas.
**Evidencia:** PR #27, PR #29 y run de CI en verde sobre `main` (commit `89ae2ff`).
