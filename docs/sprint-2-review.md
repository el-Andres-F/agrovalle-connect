# Sprint 2 Review — AgroValle Connect

**Fecha:** [completar con fecha real] · **Participantes:** [nombres] · **Rama evaluada:** `main`.

## Incremento demostrado

| Historia / entregable | Estado real | Evidencia (PR, commit, demo o prueba) | Observación |
|---|---|---|---|
| Modelo de datos y arquitectura | En curso hasta validar DDL/diagramas | [enlace] | Revisar equivalencia JPA–PostgreSQL y migración de datos existente. |
| Refactorización SOLID y patrón Strategy | En curso hasta ejecutar pruebas y revisión de pares | [enlace] | Checkout desacoplado mediante estrategia. |
| Pruebas y JaCoCo 60/60 | 53 pruebas locales aprobadas; JaCoCo 60/60 pendiente de verificación oficial | [enlace a reporte] | Las pruebas locales usan H2; ejecutar la CI con PostgreSQL y adjuntar el reporte generado por JaCoCo antes de declarar cobertura cumplida. |
| CI y protección de `main` | CI existente; protección requiere evidencia de configuración | [enlace a run y reglas] | Adjuntar captura del run verde del commit de entrega. |
| Scrumban y WIP | Pendiente de enlace público y captura | [URL del proyecto] | Mostrar explícitamente los límites WIP. |

## Demo y retroalimentación del Product Owner

- **Flujos demostrados:** [enumerar solo los realmente ejecutados].
- **Feedback recibido:** [registrar comentarios reales].
- **Historias aceptadas / no aceptadas:** [estado y criterio].
- **Decisión pendiente:** aprobar el catálogo de municipios válidos para HU-01.

## Evidencias obligatorias

- [ ] Video o capturas de la demostración funcional.
- [ ] Último run verde de GitHub Actions en `main`.
- [ ] Reporte HTML de JaCoCo con cobertura mínima de 60 % en instrucciones y ramas.
- [ ] Enlace al tablero Scrumban con límites WIP visibles.
