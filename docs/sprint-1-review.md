# Sprint 1 Review — Evidencias de la demo

> **Instrucciones:** completar con el resultado real de la demo. Adjuntar capturas en `docs/evidencias/` y enlazarlas aquí.

## Datos de la revisión

| Campo | Valor |
| --- | --- |
| Fecha | [COMPLETAR] |
| Asistentes | [COMPLETAR] |
| Sprint Goal | Registro validado y persistido de agricultores y compradores, con pruebas JUnit 5 de los criterios BDD. |

## Estado del incremento

| Historia | SP | Estado | Observación |
| --- | ---: | --- | --- |
| HU-01 — Registro de Agricultores | 5 | [Done / Parcial / No terminada] | [COMPLETAR] |
| HU-06 — Registro de Compradores | 5 | [Done / Parcial / No terminada] | [COMPLETAR] |

**Story Points completados:** [n] de 10.
**¿Se cumplió el Sprint Goal?** [Sí / Parcialmente / No] — [COMPLETAR justificación].

## Guion de la demo

1. Levantar la aplicación con `./mvnw spring-boot:run` (PostgreSQL activo).
2. Registrar un agricultor: `POST /api/v1/auth/register` con nombre, ubicación del Valle y cédula válida. Resultado esperado: `201 Created`.
3. Registrar un comprador: `POST /api/v1/auth/register` con nombre, correo, teléfono y contraseña válida. Resultado esperado: `201 Created`.
4. Repetir con datos inválidos o duplicados. Resultado esperado: `400` / `409`.
5. Verificar en PostgreSQL que los registros existen y que la contraseña está cifrada.
6. Ejecutar `./mvnw test` y mostrar las pruebas BDD en verde.

## Evidencias

| Evidencia | Enlace |
| --- | --- |
| Respuesta `201` registro de agricultor | [COMPLETAR] |
| Respuesta `201` registro de comprador | [COMPLETAR] |
| Registros en PostgreSQL | [COMPLETAR] |
| Resultado de `./mvnw test` | [COMPLETAR] |
| Pull Requests fusionados | [COMPLETAR: enlaces] |

## Retroalimentación del Product Owner

[COMPLETAR]

## Ajustes al Product Backlog

[COMPLETAR]
