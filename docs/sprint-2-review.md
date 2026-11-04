# Sprint 2 Review — AgroValle Connect

**Fecha:** 4 de noviembre de 2026 [confirmar fecha de la reunión] · **Participantes:** Andrés Camilo Fuelagan Cuastumal, Juan David Chicangana Rodríguez, Juan José Guerrero Ospina y Miguel Ángel Hurtado Arias [confirmar asistentes] · **Rama evaluada:** `main`.

## Incremento demostrado

| Entregable | Estado | Evidencia | Observación |
|---|---|---|---|
| Modelo de datos y arquitectura | Entregado | `docs/diseno/` (DER 3FN, `schema.sql`, MVC 4 capas, despliegue, secuencia, componentes, wireframes) | La equivalencia JPA–PostgreSQL se valida con el esquema versionado en `src/main/resources/schema.sql`. |
| Refactorización SOLID y patrón Strategy | Entregado | PR #29 integrado en `main`; `docs/diseno/principios-solid-strategy.md` | `PagoPseService` depende de `CheckoutStrategy`; `WompiPseCheckoutStrategy` genera el enlace de pago. |
| Pruebas y JaCoCo 60/60 | Entregado | 53 pruebas JUnit/Mockito/MockMvc; run de CI en verde sobre `main` (commit `89ae2ff`) con el paso de verificación JaCoCo aprobado | Reporte HTML disponible como artefacto `jacoco-report` del run. |
| CI | Entregado | `.github/workflows/ci.yml` (Checkstyle, pruebas, JaCoCo) | Run en verde del último commit de `main`. |
| Protección de `main` | En curso | `docs/scrumban-y-branch-protection.md` | La regla de aprobación de pares debe quedar activa y comprobarse con un PR. |
| Scrumban y WIP | Entregado con ajuste | Tablero https://github.com/users/el-Andres-F/projects/2; `docs/kanban-sprint-2.md` | Límites En curso ≤ 3 y En revisión ≤ 2 declarados en las columnas. |

## Demo y retroalimentación del Product Owner

- **Flujos demostrados:** [enumerar solo los realmente ejecutados en la demo].
- **Retroalimentación recibida:** [registrar los comentarios reales].
- **Decisión pendiente:** aprobación del catálogo de municipios válidos para HU-01 (REQ-HU01-08).

## Evidencias

- Último run en verde de GitHub Actions en `main` (Figura 1 del informe).
- Reporte HTML de JaCoCo o log del paso `jacoco:check` (Figura 2).
- Tablero con límites WIP visibles (Figura 3).
