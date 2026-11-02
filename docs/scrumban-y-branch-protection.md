# Configuración externa de GitHub — pendientes de un administrador

Estos controles pertenecen a la configuración hospedada del repositorio y no se activan al añadir archivos locales.

## GitHub Projects

El repositorio consultado no publica actualmente ningún proyecto. Un integrante con permisos debe crear el tablero Sprint 2 y configurar estas columnas/WIP:

| Estado | Límite WIP |
|---|---:|
| Backlog / Ready | Sin límite |
| In Progress | 3 |
| Code Review | 2 |
| Done | Sin límite |

Después debe publicar/compartir la URL del tablero, vincular las issues y adjuntar una captura donde se vean las columnas y límites. No se debe presentar la tabla de este archivo como evidencia de un tablero ya configurado.

## Branch protection para `main`

En **Settings → Branches → Add branch protection rule** (o Rulesets), exigir:

- patrón `main`;
- Pull Request antes de integrar cambios y al menos una aprobación;
- descartar aprobaciones obsoletas al cambiar el PR;
- exigir que las conversaciones estén resueltas;
- exigir el status check de CI que corresponda al job `build-and-test`;
- bloquear force-push y borrado de la rama; no conceder bypass salvo decisión explícita del equipo.

Guardar la regla y verificarla con un PR de prueba. Adjuntar captura de la configuración activa; un archivo del repositorio no puede demostrar ni aplicar por sí solo esta política.
