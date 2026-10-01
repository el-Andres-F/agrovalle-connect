# Planificación del Sprint 1 — AgroValle Connect

## Datos del Sprint

| Campo | Valor |
| --- | --- |
| Sprint | 1 |
| Fechas | 30/09/2026 a 14/10/2026 (2 semanas) |
| Capacidad | **10 Story Points** |
| Scrum Master | Andres Fuelagan |
| Product Owner | Juan José Guerrero |
| Equipo de desarrollo | Juan Chicangana, Andres Fuelagan, Juan José Guerrero, Miguel Hurtado |
| Dailies | Teams, 3 veces por semana (lunes, miércoles y viernes, 7:00 p. m.) |

## Sprint Goal

> Al finalizar el Sprint 1, agricultores y compradores del Valle del Cauca podrán registrarse en la plataforma mediante la API REST, con datos validados y persistidos en PostgreSQL, y con pruebas automatizadas JUnit 5 que verifican los criterios BDD de ambas historias.

## Historias seleccionadas (10 SP)

| Historia | Prioridad | SP |
| --- | --- | ---: |
| HU-01 — Registro de Agricultores | Must | 5 |
| HU-06 — Registro de Compradores | Must | 5 |
| **Total** | | **10** |

La selección respeta la capacidad y prioriza historias Must Have que no dependen de otras (ni de JWT ni de pedidos).

## Criterios BDD a implementar

**HU-01.** *Given* que el usuario ingresa a `/api/v1/auth/register`, *When* envía un JSON con nombre, ubicación del Valle y cédula válida, *Then* el sistema responde `201 Created` y el registro persiste en PostgreSQL.

**HU-06.** *Given* que el usuario ingresa a `/api/v1/auth/register`, *When* envía un JSON con nombre, correo, teléfono y contraseña válida, *Then* el sistema responde `201 Created` y almacena la cuenta en PostgreSQL.

**Decisión técnica a confirmar en la planificación:** ambas historias usan la misma ruta. Se propone un único endpoint con un campo `tipoUsuario` (`AGRICULTOR` | `COMPRADOR`) que decide qué campos son obligatorios. Alternativa: dos rutas (`/auth/register/agricultores` y `/auth/register/compradores`) ajustando el BDD.

## Descomposición técnica en tareas

Cada tarea se asocia a una característica de calidad de la norma **ISO/IEC 25010**.

| ID | Tarea (Java / Spring Boot) | HU | ISO/IEC 25010 | Estimación |
| --- | --- | --- | --- | ---: |
| T-01 | Crear entidad JPA `Comprador` (nombre, correo único, teléfono, hash de contraseña) y su `CompradorRepository`. | HU-06 | Adecuación funcional | 3 h |
| T-02 | Extender/reutilizar la entidad `Productor` con cédula y ubicación del Valle; agregar `existsByCedula`. | HU-01 | Adecuación funcional | 2 h |
| T-03 | Definir DTOs de solicitud/respuesta (`records`) con Bean Validation (`@NotBlank`, `@Email`, `@Pattern` para cédula y teléfono). | HU-01, HU-06 | Fiabilidad (validación de entrada) | 3 h |
| T-04 | Implementar `AuthController` (`POST /api/v1/auth/register`) que responde `201 Created` con el recurso creado y `Location`. | HU-01, HU-06 | Adecuación funcional | 4 h |
| T-05 | Crear `RegistroService` con la lógica de registro y validación de duplicados (cédula/correo). | HU-01, HU-06 | Mantenibilidad (modularidad) | 4 h |
| T-06 | Cifrar contraseñas con BCrypt (`spring-security-crypto`) antes de persistir; nunca devolverlas en la respuesta. | HU-06 | Seguridad (confidencialidad) | 3 h |
| T-07 | Manejador global de errores (`@RestControllerAdvice`): `400` por validación, `409` por duplicado, cuerpo JSON uniforme. | HU-01, HU-06 | Fiabilidad (tolerancia a fallos) | 3 h |
| T-08 | **Traducir los criterios BDD (Given-When-Then) de HU-01 y HU-06 a pruebas automatizadas con JUnit 5** (`@SpringBootTest` + `MockMvc`, métodos nombrados `given_when_then`, incluyendo escenarios negativos). | HU-01, HU-06 | Adecuación funcional / Fiabilidad | 6 h |
| T-09 | Configurar base H2 en perfil `test` para que las pruebas no dependan de PostgreSQL local. | HU-01, HU-06 | Mantenibilidad (capacidad de ser probado) | 2 h |
| T-10 | Cumplir `checkstyle.xml` en el código nuevo y actualizar `guia-ejecucion.md` con las nuevas rutas. | HU-01, HU-06 | Mantenibilidad (analizabilidad) | 2 h |

Total estimado: 32 h, es decir, unas 8 h por integrante en las 2 semanas del Sprint, lo que cabe dentro de la capacidad del equipo.

## Asignación de tareas

Criterios: un solo responsable por tarea, nadie con más de 3 tareas, y quien implementa no revisa su propio Pull Request.

| Tarea | Descripción | Responsable | Revisor | Horas |
| --- | --- | --- | --- | ---: |
| T-01 | Entidad JPA `Comprador` y `CompradorRepository` | Miguel Hurtado | Andres Fuelagan | 3 |
| T-02 | Extender/reutilizar `Productor` (cédula, ubicación) y `existsByCedula` | Miguel Hurtado | Juan José Guerrero | 2 |
| T-03 | DTOs (`records`) con Bean Validation | Andres Fuelagan | Juan Chicangana | 3 |
| T-04 | `AuthController` (`POST /api/v1/auth/register`) con `201` y `Location` | Andres Fuelagan | Juan José Guerrero | 4 |
| T-05 | `RegistroService` con validación de duplicados | Juan José Guerrero | Miguel Hurtado | 4 |
| T-06 | Cifrado de contraseñas con BCrypt | Miguel Hurtado | Juan Chicangana | 3 |
| T-07 | Manejador global de errores (`@RestControllerAdvice`) | Juan José Guerrero | Andres Fuelagan | 3 |
| T-08 | Pruebas JUnit 5 de los criterios BDD (`MockMvc`, `given_when_then`) | Juan Chicangana | Juan José Guerrero | 6 |
| T-09 | Base H2 en perfil `test` | Juan Chicangana | Miguel Hurtado | 2 |
| T-10 | Checkstyle y actualización de `guia-ejecucion.md` | Juan Chicangana | Andres Fuelagan | 2 |

Carga por persona: Juan Chicangana 10 h (3 tareas), Miguel Hurtado 8 h (3 tareas), Andres Fuelagan 7 h (2 tareas), Juan José Guerrero 7 h (2 tareas).

Orden sugerido: T-09, T-01, T-02 y T-03 primero; luego T-05, T-06 y T-04; después T-07; y T-08 y T-10 al final, cuando el endpoint ya exista.

## Riesgos y dependencias

- La ruta compartida `/api/v1/auth/register` requiere la decisión técnica indicada arriba.
- La cédula y el teléfono necesitan una regla de validación acordada con el Product Owner.
- HU-02 requiere JWT (autenticación), que queda fuera de este Sprint.

## Acuerdos de trabajo

- Cada tarea sigue GitFlow: rama `feature/*` desde `develop` y Pull Request revisado por otro integrante.
- Límites WIP en el tablero: **In Progress ≤ 3** y **Code Review ≤ 2**.
- Una historia solo pasa a Done cuando cumple [`docs/dod.md`](dod.md).
