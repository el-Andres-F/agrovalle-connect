# Tablero Kanban — Historias de usuario (Sprint 1 y Sprint 2)

**Tablero:** https://github.com/users/el-Andres-F/projects/2
**Columnas:** Backlog · To Do · En curso (In progress) · En revisión (In review) · Hecho (Done).
**Límites WIP:** En curso ≤ 3 · En revisión ≤ 2.
**Escala de estimación:** Story Points (Fibonacci: 1, 2, 3, 5, 8, 13). **Prioridad:** MoSCoW (Must = P0, Should = P1, Could = P2).

## Campos del tablero (como en GitHub Projects)

| Campo | Tipo | Valores |
|---|---|---|
| Title | Título de la tarjeta | `HU-XX Nombre de la historia` |
| Status | Columna | Backlog, To Do, En curso, En revisión, Hecho |
| Priority | Single select | Must (P0), Should (P1), Could (P2) |
| Estimate | Number | Story Points |
| Sprint | Iteration | Sprint 1 (30/09–14/10) · Sprint 2 (15/10–04/11) |
| Start date / Target date | Date | Ver tablas |
| Assignees | Persona | Ver tablas |

Para crear los campos: en el tablero → **⋯ → Settings → Custom fields → New field**; luego, en la vista Board, **Fields** y activar los que se quieren mostrar en cada tarjeta.

## Sprint 1 — 30 de septiembre al 14 de octubre de 2026 (capacidad: 10 SP)

| Historia de usuario | Estado | Prioridad | Estimación | Fecha de inicio | Fecha de cierre | Responsable |
|---|---|---|---:|---|---|---|
| HU-01 Registro de Agricultores | Hecho | Must (P0) | 5 | 30/09/2026 | 22/10/2026 | Andrés Camilo Fuelagan Cuastumal |
| HU-06 Registro de Compradores | Hecho | Must (P0) | 5 | 30/09/2026 | 22/10/2026 | Juan David Chicangana Rodríguez |

**Nota:** el review del Sprint 1 dejó ambas historias como *parciales* (faltaba validar la persistencia con PostgreSQL). Esa validación se cerró en el Sprint 2 con la CI ejecutando PostgreSQL, por eso el cierre es el 22/10/2026.

## Sprint 2 — 15 de octubre al 4 de noviembre de 2026

| Historia de usuario | Estado | Prioridad | Estimación | Fecha de inicio | Fecha de cierre / objetivo | Responsable |
|---|---|---|---:|---|---|---|
| HU-02 Publicación de Productos | Hecho | Must (P0) | 5 | 15/10/2026 | 22/10/2026 | Andrés Camilo Fuelagan Cuastumal |
| HU-04 Filtro de Categorías | Hecho | Must (P0) | 3 | 15/10/2026 | 22/10/2026 | Juan José Guerrero Ospina |
| HU-13 Historial de Compras | Hecho | Should (P1) | 5 | 22/10/2026 | 29/10/2026 | Miguel Ángel Hurtado Arias |
| HU-09 Pago Seguro (PSE / Wompi) | En revisión | Must (P0) | 13 | 22/10/2026 | Objetivo 04/11/2026 | Andrés Camilo Fuelagan Cuastumal |
| HU-07 Solicitar Transporte | To Do | Must (P0) | 8 | — | Sprint 3 | Juan José Guerrero Ospina |
| HU-10 Confirmación de Pago | To Do | Should (P1) | 3 | — | Sprint 3 | Miguel Ángel Hurtado Arias |
| HU-14 Notificaciones del Pedido (Observer) | To Do | Could (P2) | 5 | — | Sprint 3 | Juan David Chicangana Rodríguez |
| HU-15 Guardar Productos Favoritos | To Do | Could (P2) | 3 | — | Sprint 3 | Juan David Chicangana Rodríguez |

HU-14 cuenta con una propuesta de diseño en `docs/diseno/propuesta-observer-hu14.md`.

## Backlog (sin sprint asignado)

| Historia de usuario | Estado | Prioridad | Estimación |
|---|---|---|---:|
| HU-03 Visualización de Precios Regionales | Backlog | Should (P1) | 8 |
| HU-05 Contacto Directo | Backlog | Should (P1) | 5 |
| HU-08 Seguimiento del Envío | Backlog | Should (P1) | 5 |
| HU-11 Calificación del Vendedor | Backlog | Should (P1) | 3 |
| HU-12 Calificación del Comprador | Backlog | Could (P2) | 3 |

## Resumen de flujo

| Sprint | Planificado | Hechas | En revisión | Pendientes |
|---|---:|---:|---:|---:|
| Sprint 1 | 10 SP | 10 SP (HU-01, HU-06) | 0 | 0 |
| Sprint 2 | 26 SP (HU-02, HU-04, HU-09, HU-13) | 13 SP (HU-02, HU-04, HU-13) | 13 SP (HU-09) | 19 SP (HU-07, HU-10, HU-14, HU-15) |

**Límites WIP al 4 de noviembre:** En curso 0 / límite 3; En revisión 1 / límite 2.
