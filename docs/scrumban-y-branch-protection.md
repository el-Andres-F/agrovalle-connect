# Scrumban y Branch Protection — configuración en GitHub

Estos controles pertenecen a la configuración hospedada de GitHub y no se activan con archivos del repositorio. Este documento registra el estado y los pasos.

## GitHub Projects

- **Tablero existente:** https://github.com/users/el-Andres-F/projects/2 (columnas Backlog, To Do, En curso, En revisión y Hecho).
- **Pendiente:** declarar los límites WIP en el nombre/descripción de las columnas y actualizar las tarjetas según `docs/kanban-sprint-2.md`.

| Estado | Límite WIP |
|---|---:|
| Backlog / To Do | Sin límite |
| En curso (In progress) | 3 |
| En revisión (In review) | 2 |
| Hecho (Done) | Sin límite |

## Branch protection para `main`

Ruta: **Settings → Branches → Add branch protection rule** (o **Rules → Rulesets**).

1. Patrón de rama: `main`.
2. Activar **Require a pull request before merging** con **1 aprobación** requerida.
3. Activar **Dismiss stale pull request approvals when new commits are pushed**.
4. Activar **Require status checks to pass before merging** y seleccionar el check `build-and-test` (aparece después de haber corrido el workflow).
5. Activar **Require conversation resolution before merging**.
6. Activar **Do not allow bypassing the above settings** (incluye administradores) y bloquear force-push y eliminación.
7. Guardar y verificar con un PR de prueba que otro integrante apruebe.

## Estado verificado en el historial público (4 de noviembre de 2026)

- El workflow `ci.yml` terminó en verde sobre `main` (commit `89ae2ff`, integración del PR #29).
- El PR #29 se integró sin revisiones registradas, por lo que la aprobación de pares aún no estaba exigida. Esa regla debe activarse y comprobarse con el siguiente PR antes de afirmar que está vigente.
