# Bitácora de Daily Scrums — AgroValle Connect

> **Nota para el equipo (borrar antes de entregar):** las fechas, horas, asistentes, roles, impedimentos y acuerdos son los que indicó el equipo. La evidencia de cada reunión proviene del repositorio (commits, Pull Requests y tablero). Llenen los pocos campos `[CONFIRMAR]` que quedan y verifiquen entre los cuatro que todo coincida con lo que realmente ocurrió.

**Equipo:** Juan Chicangana, Andres Fuelagan, Juan José Guerrero, Miguel Hurtado
**Formato:** cada integrante responde (1) qué hizo desde la última reunión, (2) qué hará a continuación, (3) qué impedimentos tiene.

**Sprint Goal del Sprint 1:** agricultores y compradores pueden registrarse en la plataforma con datos validados y persistidos, con pruebas JUnit 5 de los criterios BDD.

---

## Reunión #1 — 13 de septiembre de 2026, 9:00 p. m. (Sprint 0: arranque)

**Medio:** [CONFIRMAR]
**Asistentes:** Juan Chicangana, Andres Fuelagan, Juan José Guerrero y Miguel Hurtado.
**Dinámica:** Andres realizó el trabajo; los demás integrantes lo supervisaron y lo probaron.

**Evidencia en el repositorio (13/09):** `chore: inicializar estructura de carpetas y gitignore`, `docs: actualizar readme para evidencia de PR`, `Refactor README.md to streamline content`, `Add files via upload`.

| Integrante | ¿Qué hice? | ¿Qué haré? | Impedimentos |
| --- | --- | --- | --- |
| Andres Fuelagan | Inicializó la estructura de carpetas y el `.gitignore`; subió la primera versión del README. | Dejar listos el Backlog de 15 HU, el DoD, Checkstyle, las APIs de productos y productores, y la guía de ejecución. | Ninguno |
| Juan Chicangana | Supervisó y probó lo realizado por Andres. | Asignar los valores (Story Points) a las historias de usuario. | Ninguno |
| Juan José Guerrero | Supervisó y probó lo realizado por Andres. | Supervisar y probar lo realizado por Andres y Juan Chicangana. | Ninguno |
| Miguel Hurtado | Supervisó y probó lo realizado por Andres. | Supervisar y probar lo realizado por Andres y Juan Chicangana. | Ninguno |

**Acuerdos:** Ninguno.

---

## Reunión #2 — 20 de septiembre de 2026, 8:00 p. m., Microsoft Teams (Sprint 0: seguimiento)

**Asistentes:** Juan Chicangana, Andres Fuelagan, Juan José Guerrero y Miguel Hurtado.
**Dinámica:** Andres y Juan Chicangana realizaron el trabajo; Miguel y Juan José lo supervisaron y lo probaron.

**Evidencia en el repositorio (14/09 al 24/09):** README actualizado y fusionado en el PR #1 (14/09); Backlog de 15 historias de usuario, Definition of Done y automatización de Checkstyle (15/09); APIs de productos y productores con pruebas (15/09); corrección de normalización de categorías y guía de ejecución (16 y 17/09); descripciones de tareas y Story Points en el BACKLOG (24/09).

| Integrante | ¿Qué hice? | ¿Qué haré? | Impedimentos |
| --- | --- | --- | --- |
| Andres Fuelagan | Dejó listos el Backlog de 15 HU, el DoD, Checkstyle, las APIs de productos y productores, y la guía de ejecución. | Elaborar el tablero Kanban junto con Juan José Guerrero y revisar y fusionar el PR #25 en `develop`. | Ninguno |
| Juan Chicangana | Asignó los valores (Story Points) a las historias de usuario. | [CONFIRMAR] | Ninguno |
| Juan José Guerrero | Supervisó y probó lo realizado por Andres y Juan Chicangana. | Elaborar el tablero Kanban junto con Andres, organizar los requisitos del trabajo e informarlos al equipo. | Ninguno |
| Miguel Hurtado | Supervisó y probó lo realizado por Andres y Juan Chicangana. | Corregir el tablero Kanban, preparar la planificación del Sprint 1 y los documentos de ejecución, activar Husky y abrir el PR #25. | Ninguno |

**Acuerdos:** Ninguno.

---

## Reunión #3 — 30 de septiembre de 2026, desde las 5:00 p. m., Microsoft Teams (Sprint 1: día 1)

**Asistentes:** Andres Fuelagan, Juan José Guerrero y Miguel Hurtado. No asistió Juan Chicangana.

**Evidencia en el repositorio (30/09):** creación de la rama `develop`; commit `docs: agregar planificacion del Sprint 1 y activar Husky` en `feature/sprint-1-planning`; Pull Request #25 con revisión solicitada a los otros tres integrantes y fusionado en `develop`; tablero Kanban en GitHub Projects con las columnas Backlog, To Do, En curso, En revisión y Hecho.

| Integrante | ¿Qué hice? | ¿Qué haré? | Impedimentos |
| --- | --- | --- | --- |
| Juan José Guerrero | Elaboró el tablero Kanban junto con Andres; organizó los requisitos del trabajo y los informó al equipo. | Investigar cómo elaborar la documentación solicitada en el formato permitido. | Ninguno |
| Andres Fuelagan | Elaboró el tablero Kanban junto con Juan José Guerrero; revisó y fusionó el PR #25 en `develop`. | [CONFIRMAR] | Ninguno |
| Miguel Hurtado | Corrigió el tablero Kanban; preparó la planificación del Sprint 1 y los documentos de ejecución, activó Husky y abrió el PR #25. | Ajustar el límite de Code Review a 2, depurar tarjetas duplicadas del tablero y definir un Sprint 1 de 10 Story Points. | El hook de pre-commit falló por la conexión a PostgreSQL (base y contraseña sin configurar); ya se resolvió. |
| Juan Chicangana | No asistió. | — | — |

**Estado del tablero:** En curso 0 / 3 · En revisión 0 · Hecho 2 (HU-01 y HU-02, por revisar frente a su DoD).
**Acuerdos:** Ninguno.

---

## Resumen de impedimentos

| Fecha | Impedimento | Responsable de resolverlo | Estado |
| --- | --- | --- | --- |
| 30/09 | El hook de pre-commit fallaba porque no existía la base `agrovalle` ni la contraseña de PostgreSQL configurada. | Miguel Hurtado | Resuelto |
| 30/09 | Git no tenía la identidad del autor configurada, lo que impedía hacer commits. | Miguel Hurtado | Resuelto |
