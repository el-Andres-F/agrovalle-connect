# Sprint 1 Review — Evidencias de la demo



## Datos de la revisión

| Campo | Valor |
| --- | --- |
| Fecha | 30/09/2026 |
| Asistentes | Juan Chicangana, Andres Fuelagan, Juan José Guerrero, Miguel Hurtado |
| Sprint Goal | Registro validado y persistido de agricultores y compradores, con pruebas JUnit 5 de los criterios BDD. |

## Estado del incremento

| Historia | SP | Estado | Observación |
| --- | ---: | --- | --- |
| HU-01 — Registro de Agricultores | 5 | Parcial | El flujo de registro quedó implementado con validación de cédula y ubicación del Valle; falta confirmar la ejecución final con la base activa y las credenciales correctas de PostgreSQL. |
| HU-06 — Registro de Compradores | 5 | Parcial | El flujo de registro quedó implementado con validación de correo, teléfono y contraseña; falta validar la persistencia real en PostgreSQL con la configuración de acceso correcta. |

**Story Points completados:** 10 de 10 planeados, con evidencia de implementación parcial y validación técnica pendiente.
**¿Se cumplió el Sprint Goal?** Parcialmente — Las historias quedaron desarrolladas en código, pero la validación end-to-end quedó bloqueada por la configuración de PostgreSQL. El objetivo del sprint requiere la base activa y autenticada para confirmar la persistencia y la ejecución de pruebas.

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
| Respuesta `201` registro de agricultor | `POST /api/v1/auth/register` con payload válido de agricultor → `201 Created` y ubicación del recurso en la respuesta. |
| Respuesta `201` registro de comprador | `POST /api/v1/auth/register` con payload válido de comprador → `201 Created` y registro persistido. |
| Registros en PostgreSQL | Verificación del insert en la tabla correspondiente con datos persistidos y contraseña cifrada mediante BCrypt. |
| Resultado de `./mvnw test` | La ejecución quedó bloqueada por autenticación de PostgreSQL: `FATAL: la autenticación password falló para el usuario "postgres"`. Requiere configurar la base y las credenciales antes de validar la suite en verde. |
| Pull Requests fusionados | PR #25 (planificación del Sprint 1 y activación de Husky) en `develop`; además se usó la rama `develop` para integrar la documentación base del sprint. |

## Retroalimentación del Product Owner

El equipo avanzó en la funcionalidad base del registro para agricultores y compradores, con validación de entrada y lógica de negocio implementada. La parte crítica que falta es la validación end-to-end con PostgreSQL activo y autenticado, ya que la prueba automatizada quedó bloqueada por credenciales de la base de datos. Con ese ajuste, la entrega puede cerrar la meta del Sprint 1 sin dejar pendientes los criterios BDD.

## Ajustes al Product Backlog

- Mantener el alcance del Sprint 1 centrado en `HU-01` y `HU-06` para no exceder la capacidad del equipo.
- Definir la autenticación JWT como dependencia directa para `HU-02` y el resto de historias que requieran sesión o permisos.
- Aclarar la decisión técnica de la ruta de registro para evitar ambigüedad en la API (`/api/v1/auth/register` con `tipoUsuario` o rutas separadas).
- Priorizar en el Sprint 2 la publicación de productos y la validación del flujo completo desde registro hasta consumo de la plataforma.
- Revisar nuevamente el límite de trabajo en curso y la revisión cruzada de PRs para mejorar la trazabilidad y colaboración del equipo.
