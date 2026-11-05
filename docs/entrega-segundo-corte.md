# GrupoXX_Entrega_Segundo_Corte_AgroValle

> Borrador de armado. Adjuntar las capturas reales antes de exportar a PDF.

## Portada institucional

- Institución Universitaria Antonio José Camacho — Facultad de Ingeniería — Ingeniería de Sistemas.
- Proyecto: AgroValle Connect.
- Célula Scrum: [número].
- Integrantes y documentos: [nombres completos y números, según formato autorizado].
- Asignatura/docente/período: Ingeniería de Software II / Paola Andrea Bedoya Toro / Período 2-2026.

## Enlaces públicos

- Repositorio y rama `main`: https://github.com/el-Andres-F/agrovalle-connect/tree/main
- Tablero GitHub Projects con WIP visible: https://github.com/users/el-Andres-F/projects/2

## Evidencias de aceptación

- CI: [captura del último run verde de `ci.yml` en `main`, después de integrar los cambios].
- JaCoCo: [captura del informe HTML mostrando al menos 60 % de instrucciones y 60 % de ramas].
- Branch protection: [captura de reglas activas para `main`].
- Demo Sprint 2: [capturas o evidencia de los flujos realmente ejecutados].

## Resumen de cambios Sprint 2 (máximo dos páginas)

- Modelo relacional y DDL PostgreSQL: ver `docs/diseño/modelo-relacional.md` y `src/main/resources/schema.sql`.
- Arquitectura de checkout: `PagoPseController` delega en `PagoPseService`, que depende de `CheckoutStrategy`; Wompi implementa la generación de checkout.
- Normalización: los productos de cada pedido se guardan en `pedido_detalles`, no como lista embebida en el campo del pedido.
- Pruebas: 53 pruebas JUnit/Mockito/MockMvc locales aprobadas (H2). Añadir el run CI y reporte JaCoCo 60/60 ejecutados sobre el commit final con PostgreSQL antes de entregar.
- Lecciones aprendidas: integrar solo por Pull Request revisado por un par; mantener la CI como puerta de calidad (Checkstyle, pruebas, JaCoCo); separar capas para poder probar con Mockito; actualizar el tablero y mostrar los límites WIP (ver `docs/sprint-2-retrospective.md`).

## Checklist de carga

- [ ] Confirmar municipio/regla de negocio de HU-01 con Product Owner.
- [ ] Ejecutar build, Checkstyle, suite de pruebas y JaCoCo en el commit final.
- [ ] Confirmar branch protection en GitHub y aprobación por pares.
- [ ] Configurar y enlazar el tablero Scrumban con límites WIP explícitos.
- [ ] Completar bitácora, revisión y retrospectiva con hechos/evidencia real.
- [ ] Sustituir marcadores, exportar este documento a un único PDF y cargarlo antes de la fecha límite.
