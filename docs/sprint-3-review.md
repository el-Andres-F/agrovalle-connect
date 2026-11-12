# Sprint 3 Review — AgroValle Connect

**Fecha:** 12 de noviembre de 2026 · **Rama evaluada:** `main` · **Laboratorio:** Despliegue Continuo (CD), GitHub Secrets y ADRs (Sesión 12).

## Incremento demostrado

| Entregable | Estado | Evidencia | Observación |
|---|---|---|---|
| Perfil de Staging | Entregado | `src/main/resources/application-staging.yml` | Credenciales de BD y administrador desde variables de entorno. |
| GitHub Secrets | _Pendiente de confirmar_ | Settings > Secrets and variables > Actions | `STAGING_DB_URL`, `STAGING_DB_USER`, `STAGING_DB_PASSWORD`, `JWT_SECRET_KEY`, `DEPLOY_HOOK_URL`. |
| Pipeline CD | _Pendiente de confirmar_ | `.github/workflows/cd.yml` | Pegar aquí el enlace al run en verde. |
| ADR | Entregado | `docs/architecture-decisions/ADR-001-despliegue-staging-paas.md` | Plantilla Nygard. |
| Endpoint de verificación | Entregado | `GET /api/v1/estado` (`EstadoController`) | Cubierto por `EstadoControllerTest`. |

## Evidencia en vivo (Staging)

- **URL pública de Staging:** _(pegar aquí)_
- **Comando ejecutado:**

```bash
curl -X GET https://<url-de-staging>/api/v1/estado
```

- **Respuesta HTTP 200 OK obtenida:** _(pegar aquí la salida real o adjuntar captura)_

```json
{
  "aplicacion": "AgroValle Connect",
  "perfil": "staging",
  "estado": "OK",
  "baseDatos": "PostgreSQL Connected"
}
```

- **Prueba de escritura:** `POST /api/v1/auth/register` con el DTO de comprador → _(resultado)_
- **Run en verde de GitHub Actions (`cd.yml`):** _(enlace)_
