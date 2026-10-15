# HU-01 en IEEE 29148 vs BDD — AgroValle Connect

Rúbrica Sprint de Diseño y Sprint 1 · Componente 4 (15 %): reescritura formal de la HU-01 en el estándar IEEE 29148 y cuadro comparativo frente a la agilidad de BDD.

Equipo: Juan Chicangana, Andres Fuelagan, Juan Guerrero, Miguel Hurtado

## 1. Introducción

### 1.1 Propósito

Este documento reescribe la historia de usuario **HU-01 — Registro de Agricultores** como un conjunto de requisitos formales siguiendo la norma ISO/IEC/IEEE 29148:2018 (Ingeniería de requisitos), y la contrasta con su especificación original en formato BDD (Given-When-Then) del `BACKLOG.md`.

### 1.2 Historia original (Product Backlog)

> **Como** agricultor, **quiero** registrarme en la plataforma **para** ofrecer mis productos. Prioridad: Must. Story Points: 5.
>
> **Given** que el usuario ingresa a `/api/v1/auth/register`. **When** envía un JSON con nombre, ubicación del Valle y cédula válida. **Then** el sistema responde con `201 Created` y el registro persiste en PostgreSQL.

### 1.3 Alcance

La especificación cubre únicamente el registro de productores (agricultores) mediante la API REST de AgroValle Connect. El registro de compradores (HU-06) comparte el endpoint, pero queda fuera de este documento.

### 1.4 Definiciones

| Término | Definición |
| --- | --- |
| Productor / agricultor | Persona que ofrece productos agrícolas en la plataforma. En el código es la entidad `Productor` (tabla `productores`). |
| Cédula válida | Cadena de entre 6 y 10 dígitos numéricos (regla `^\d{6,10}$` de `RegistroService`). |
| Ubicación del Valle | Municipio del departamento del Valle del Cauca. |
| TBD | Información por determinar. La norma 29148 exige registrarla y darle seguimiento hasta su resolución. |
| Sistema | La aplicación AgroValle Connect (Spring Boot, Java 17, PostgreSQL). |

## 2. Requisito de las partes interesadas (StRS)

| Campo | Valor |
| --- | --- |
| ID | STK-01 |
| Parte interesada | Agricultor del Valle del Cauca |
| Necesidad | Contar con una cuenta propia para publicar sus cosechas y venderlas sin intermediarios. |
| Justificación | Declaración de visión del producto: vender directo a precio justo. |
| Prioridad | Alta (Must) |

## 3. Requisitos del sistema (SRS)

Redactados con la sintaxis recomendada por la norma: *[Condición] El sistema deberá [acción] [objeto] [restricción]*. Cada requisito es singular (una sola obligación), verificable y tiene un método de verificación asignado: **T** = Test (prueba automatizada), **I** = Inspección, **D** = Demostración, **A** = Análisis.

### 3.1 Requisitos funcionales

| ID | Requisito | Prioridad | Verif. |
| --- | --- | --- | --- |
| REQ-HU01-01 | El sistema deberá exponer el recurso `POST /api/v1/auth/register` que reciba un cuerpo JSON con los campos `tipo`, `nombre`, `ubicacion` y `cedula`. | Must | T |
| REQ-HU01-02 | Cuando `tipo` sea `PRODUCTOR`, el sistema deberá exigir que `nombre` no sea nulo ni esté en blanco. | Must | T |
| REQ-HU01-03 | Cuando `tipo` sea `PRODUCTOR`, el sistema deberá exigir que `ubicacion` no sea nula ni esté en blanco. | Must | T |
| REQ-HU01-04 | Cuando `tipo` sea `PRODUCTOR`, el sistema deberá validar que `cedula` contenga únicamente de 6 a 10 dígitos numéricos. | Must | T |
| REQ-HU01-05 | Cuando `tipo` sea `PRODUCTOR`, el sistema deberá validar que `ubicacion` corresponda a un municipio del Valle del Cauca. **(TBD: lista oficial de municipios; ver sección 6)** | Must | T |
| REQ-HU01-06 | El sistema deberá rechazar el registro de un productor cuya `cedula` ya exista, respondiendo `409 Conflict`. | Must | T |
| REQ-HU01-07 | Cuando todos los datos sean válidos, el sistema deberá persistir el productor en la tabla `productores` de PostgreSQL y asignarle un identificador único. | Must | T |
| REQ-HU01-08 | Cuando el registro sea exitoso, el sistema deberá responder `201 Created` con un cuerpo JSON que contenga `id`, `tipo` y `nombre`. | Must | T |
| REQ-HU01-09 | Cuando algún dato no cumpla las reglas de validación, el sistema deberá responder `400 Bad Request` con un cuerpo JSON de la forma `{"error": "<mensaje>"}` y no deberá persistir ningún dato. | Must | T |
| REQ-HU01-10 | Cuando el campo `tipo` falte, el sistema deberá responder `400 Bad Request` indicando que `tipo` es obligatorio. | Must | T |

### 3.2 Requisitos de calidad (ISO/IEC 25010)

| ID | Característica | Requisito | Verif. |
| --- | --- | --- | --- |
| REQ-HU01-11 | Integridad de datos | El sistema deberá ejecutar el registro dentro de una transacción, de modo que un fallo no deje datos parciales. | I |
| REQ-HU01-12 | Integridad de datos | El sistema deberá garantizar la unicidad de la cédula también a nivel de base de datos mediante una restricción `UNIQUE`. | I |
| REQ-HU01-13 | Mantenibilidad | El código del registro deberá superar `./mvnw checkstyle:check` con cero errores. | T |
| REQ-HU01-14 | Fiabilidad | La suite JUnit 5 que verifica los requisitos REQ-HU01-01 a REQ-HU01-10 deberá ejecutarse con el 100 % de pruebas aprobadas. | T |

### 3.3 Interfaz externa

**Solicitud**

```json
POST /api/v1/auth/register
Content-Type: application/json

{
  "tipo": "PRODUCTOR",
  "nombre": "Carlos Pérez",
  "ubicacion": "Candelaria",
  "cedula": "1112345678"
}
```

**Respuestas**

| Caso | Código HTTP | Cuerpo |
| --- | --- | --- |
| Registro exitoso | 201 Created | `{"id": 1, "tipo": "PRODUCTOR", "nombre": "Carlos Pérez"}` |
| Dato inválido o `tipo` ausente | 400 Bad Request | `{"error": "La cedula debe tener entre 6 y 10 digitos."}` |
| Cédula duplicada | 409 Conflict | `{"error": "Ya existe un productor con esa cedula."}` |

### 3.4 Restricciones

- Java 17, Spring Boot, Maven y PostgreSQL (declarados en el `README.md`).
- Arquitectura MVC en capas: controlador, servicio, repositorio y modelo.
- Convención de Conventional Commits y Pull Request con revisión de un par (Definition of Done).

## 4. Verificación y trazabilidad

La norma exige que cada requisito sea trazable hacia su fuente y hacia su verificación. Los nombres de pruebas siguen la convención `given_when_then` definida en la planificación del Sprint 1.

| Requisito | Fuente | Escenario BDD asociado | Prueba JUnit 5 propuesta |
| --- | --- | --- | --- |
| REQ-HU01-01, 07, 08 | STK-01 | Registro exitoso | `givenProductorValido_whenRegistrar_thenRetorna201` |
| REQ-HU01-02, 03 | STK-01 | Datos obligatorios vacíos | `givenNombreVacio_whenRegistrar_thenRetorna400` |
| REQ-HU01-04 | STK-01 | Cédula inválida | `givenCedulaInvalida_whenRegistrar_thenRetorna400` |
| REQ-HU01-05 | STK-01 | Ubicación fuera del Valle | `givenUbicacionFueraDelValle_whenRegistrar_thenRetorna400` |
| REQ-HU01-06 | STK-01 | Cédula duplicada | `givenCedulaExistente_whenRegistrar_thenRetorna409` |
| REQ-HU01-09, 10 | STK-01 | Formato uniforme de error | `givenTipoAusente_whenRegistrar_thenRetorna400` |

## 5. Cuadro comparativo: IEEE 29148 vs BDD

### 5.1 La misma exigencia en ambos formatos

**BDD (Gherkin)**

```gherkin
Funcionalidad: Registro de agricultores

  Escenario: Registro exitoso
    Dado que el usuario ingresa a /api/v1/auth/register
    Cuando envía un JSON con nombre, ubicación del Valle y cédula válida
    Entonces el sistema responde 201 Created
    Y el registro persiste en PostgreSQL

  Escenario: Cédula duplicada
    Dado que ya existe un productor con la cédula "1112345678"
    Cuando se envía otro registro con esa misma cédula
    Entonces el sistema responde 409 Conflict

  Escenario: Cédula con formato inválido
    Cuando se envía una cédula con letras o menos de 6 dígitos
    Entonces el sistema responde 400 Bad Request
```

**IEEE 29148**: REQ-HU01-04 y REQ-HU01-06 (sección 3.1), con ID, prioridad, método de verificación y fuente.

### 5.2 Comparación por criterio

| Criterio | IEEE 29148 | BDD (Given-When-Then) |
| --- | --- | --- |
| Naturaleza | Estándar internacional de ingeniería de requisitos (ISO/IEC/IEEE 29148:2018). | Práctica ágil de especificación por ejemplos, derivada de TDD. |
| Objetivo principal | Definir *qué* debe cumplir el sistema de forma no ambigua, completa y verificable. | Crear entendimiento compartido entre negocio, desarrollo y pruebas mediante ejemplos concretos. |
| Lenguaje | Formal y normativo: «El sistema deberá…». Un requisito, una obligación. | Natural y estructurado: Given / When / Then, legible para el Product Owner. |
| Unidad de especificación | Requisito individual con ID y atributos (prioridad, fuente, verificación, estado). | Escenario de comportamiento con precondición, acción y resultado. |
| Nivel de detalle | Alto: cubre funcionales, calidad, interfaces y restricciones. | Medio: se centra en comportamiento observable; los no funcionales quedan poco cubiertos. |
| Ambigüedad | Baja, por las reglas de calidad (singular, verificable, sin términos vagos). | Media: depende de qué tan precisos sean los ejemplos (en HU-01, «ubicación del Valle» es ambigua). |
| Verificabilidad | Cada requisito declara su método (Test, Inspección, Demostración, Análisis). | Cada escenario se convierte directamente en una prueba. |
| Ejecutabilidad | No es ejecutable; las pruebas se derivan después de forma manual. | Puede ser ejecutable (Cucumber) o traducirse a JUnit 5 + `MockMvc`, como hace el proyecto. |
| Trazabilidad | Formal y bidireccional: necesidad, requisito, diseño, prueba. | Implícita: historia, escenario, prueba. Rastreabilidad ligera. |
| Gestión del cambio | Cambiar un requisito exige actualizar documento, matriz y líneas base. Más lento. | Un escenario se edita o agrega en minutos durante el refinamiento. Más ágil. |
| Esfuerzo de elaboración | Alto: más tiempo de redacción y revisión. | Bajo a medio: se redacta junto al backlog. |
| Documentación viva | Tiende a desactualizarse si no se mantiene. | Se mantiene sincronizada si las pruebas son ejecutables y corren en CI. |
| Audiencia | Ingenieros de requisitos, auditores, interventoría, contratos. | Product Owner, desarrolladores y testers. |
| Ajuste con Scrum | Bajo si se usa como documento único y extenso; adecuado como línea base. | Alto: encaja con historias, criterios de aceptación y Definition of Done. |
| Riesgo principal | Burocracia y documentación que nadie lee. | Escenarios incompletos que pasan por alto reglas de negocio y requisitos no funcionales. |
| Herramientas típicas | Plantillas SRS, gestores de requisitos (Jira, DOORS). | Cucumber, JBehave, JUnit 5 con nombres `given_when_then`. |

### 5.3 Aplicación a HU-01: lo que cada enfoque aporta y lo que deja por fuera

| Aspecto de HU-01 | En el BDD original | En IEEE 29148 |
| --- | --- | --- |
| Ubicación del Valle | Mencionada sin definición. | Detectada como ambigua; se registra como TBD (REQ-HU01-05). |
| Cédula válida | «Cédula válida» sin regla. | Regla explícita: 6 a 10 dígitos (REQ-HU01-04). |
| Casos de error 400 y 409 | No aparecen en el criterio original. | Requisitos propios (REQ-HU01-06, 09, 10). |
| Integridad en base de datos | Solo «persiste en PostgreSQL». | Transaccionalidad y restricción `UNIQUE` (REQ-HU01-11, 12). |
| Calidad de código | No cubierto. | Requisitos de Checkstyle y pruebas (REQ-HU01-13, 14). |
| Comprensión del negocio | Muy clara para el Product Owner. | Más densa; requiere lectura técnica. |
| Conversión a pruebas | Inmediata. | Requiere derivar escenarios (sección 4). |

## 6. Observaciones sobre el estado actual del repositorio

Al contrastar la especificación con el código de la rama `main`, se encontraron cuatro brechas que conviene corregir antes de la entrega:

1. **Ubicación del Valle sin validar.** `RegistroService.registrarProductor` solo comprueba que `ubicacion` no esté vacía; acepta cualquier texto. El Sprint 1 Review afirma que existe validación de ubicación del Valle. Para cumplir REQ-HU01-05 hay que definir la lista de municipios (el Valle del Cauca tiene 42) y validarla.
2. **Unicidad solo en el servicio.** La entidad `Productor` no declara `@Column(unique = true)` sobre `cedula`; la unicidad depende de `existsByCedula`, lo que permite duplicados ante dos solicitudes simultáneas (REQ-HU01-12). Además, `nombre`, `ubicacion` y `cedula` no son `nullable = false`.
3. **Sin pruebas de registro.** Los únicos tests presentes son `ConnectApplicationTests` y `ProductApiIntegrationTest`, y ambos solo verifican que el contexto de Spring carga. Los escenarios de la sección 4 aún no existen como pruebas JUnit 5, y el Sprint 1 Review reporta que `./mvnw test` quedó bloqueado por la autenticación de PostgreSQL. La planificación contemplaba una base H2 en perfil `test` (T-09) para evitar esa dependencia.
4. **Respuesta sin `Location`.** La planificación (T-04) pedía `201 Created` con cabecera `Location`; `AuthController` no la envía. No es parte de los requisitos aquí definidos, pero conviene decidir si se mantiene o se retira de la planificación.

## 7. Conclusión y recomendación

IEEE 29148 y BDD no compiten: responden preguntas distintas. 29148 aporta **precisión, completitud y trazabilidad**: en HU-01 permitió descubrir la ambigüedad de «ubicación del Valle», los casos de error omitidos y los requisitos de integridad que el criterio BDD original no mencionaba. BDD aporta **agilidad, comprensión compartida y pruebas ejecutables**, que es lo que mejor se ajusta a un equipo pequeño de Scrum sin contrato formal.

Para AgroValle Connect se recomienda un enfoque híbrido: mantener BDD como criterio de aceptación en cada historia (rápido de cambiar y convertible en JUnit 5), y usar 29148 para formalizar las historias *Must* críticas, como HU-01 y HU-06, donde un error de validación o de integridad de datos tiene mayor costo. La matriz de la sección 4 conecta ambos mundos: cada requisito formal termina en un escenario BDD y en una prueba automatizada.
