# Tablero Kanban Sprint 2 — Estado y plan de tarjetas

**Tablero:** https://github.com/users/el-Andres-F/projects/2 (Tablero Kanban, GitHub Projects).
**Columnas:** Backlog · To Do · En curso (In progress) · En revisión (In review) · Hecho (Done).
**Límites WIP acordados:** En curso ≤ 3 · En revisión ≤ 2.

## Cómo hacer visibles los límites WIP

GitHub Projects no bloquea tarjetas por límite, por lo que el equipo los declara en la propia columna y los respeta manualmente:

1. En la vista Board, abrir el menú (⋯) de la columna **En curso** → *Edit details*.
2. Nombre: `En curso (In progress) · WIP máx. 3`. Descripción: `Límite de trabajo en progreso: 3 tarjetas`.
3. Repetir en **En revisión**: `En revisión (In review) · WIP máx. 2`.
4. Tomar la captura con las columnas y sus límites a la vista (Figura 3 del informe).

## Estado de las tarjetas al 4 de noviembre de 2026

### Hecho (Done)

| Tarjeta | Evidencia en el repositorio |
|---|---|
| Modelo de datos: DER en 3FN, `schema.sql` PostgreSQL y entidades JPA | `docs/diseno/der.md`, `docs/diseno/modelo-relacional.md`, `docs/diseno/schema.sql`, `src/main/resources/schema.sql` |
| Arquitectura MVC en 4 capas, despliegue, secuencia, componentes y wireframes | `docs/diseno/` |
| Reescritura de HU-01 bajo ISO/IEC/IEEE 29148 y matriz frente a BDD | `docs/ieee29148-req01.md`, `docs/HU-01-IEEE29148-vs-BDD.md` |
| Refactorización SOLID y patrón Strategy (`CheckoutStrategy`) | PR #29 integrado en `main`; `docs/diseno/principios-solid-strategy.md` |
| Suite de pruebas JUnit 5 + Mockito + MockMvc | `src/test/java/...` (53 pruebas) |
| Pipeline de CI y umbral JaCoCo 60 % | `.github/workflows/ci.yml`, `pom.xml`; run en verde sobre `main` (commit `89ae2ff`) |

### En curso (máx. 3)

| Tarjeta | Responsable |
|---|---|
| Configurar Branch Protection en `main` (PR obligatorio, 1 aprobación, status check de CI) | [administrador del repositorio] |
| Completar bitácora, review y retrospectiva del Sprint 2 con hechos reales | [integrante] |
| Reunir evidencias: run verde de CI, reporte JaCoCo y captura del tablero | [integrante] |

### En revisión (máx. 2)

| Tarjeta | Revisor |
|---|---|
| PR con la documentación del Sprint 2 (`docs/`) | [par distinto al autor] |
| Informe PDF `GrupoXX_Entrega_Segundo_Corte_AgroValle.pdf` | [par distinto al autor] |

### To Do

| Tarjeta | Nota |
|---|---|
| Confirmar con el Product Owner el catálogo de municipios de HU-01 (REQ-HU01-08) | Decisión pendiente; no se codifica una lista supuesta |
| Cargar el PDF en la plataforma institucional | Fecha límite: 4 de noviembre de 2026, 23:59 |

### Backlog (siguiente sprint)

- Segunda implementación de `CheckoutStrategy` con política de selección de proveedor.
- Mover las credenciales por defecto de `application.properties` a variables de entorno.
- Historias restantes del Product Backlog (las 14 tarjetas que ya están en la columna Backlog).

## Verificación de límites

Con la distribución anterior: En curso = 3 (límite 3) y En revisión = 2 (límite 2). Cada tarjeta que cambie de columna debe respetar esos máximos.
