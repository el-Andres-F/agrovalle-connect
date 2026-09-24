# Product Backlog — AgroValle Connect

Backlog inicial del proyecto AgroValle Connect, construido a partir de las 15 historias de usuario definidas para el Sprint 0.

## Criterios de priorización

Se utiliza MoSCoW:

- **Must:** indispensable para el funcionamiento principal del producto.
- **Should:** importante, pero puede desarrollarse después de las funciones esenciales.
- **Could:** deseable y de menor urgencia.
- **Won't:** no se contempla en el alcance actual.

## Escala de estimación

Los Story Points representan complejidad, esfuerzo, dependencias e incertidumbre. Se utiliza la escala de Fibonacci: **1, 2, 3, 5, 8 y 13**.

## Historias de usuario

### HU-01 — Registro de Agricultores

**Como** agricultor, **quiero** registrarme en la plataforma **para** ofrecer mis productos.

- **Prioridad:** Must
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que el usuario ingresa a `/api/v1/auth/register`.
- **When** envía un JSON con nombre, ubicación del Valle y cédula válida.
- **Then** el sistema responde con `201 Created` y el registro persiste en PostgreSQL.

### HU-02 — Publicación de Productos

**Como** agricultor, **quiero** publicar mis cosechas **para** que sean visibles para los compradores.

- **Prioridad:** Must
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** un agricultor autenticado con token JWT.
- **When** publica un producto con tipo, cantidad y fecha de cosecha.
- **Then** el sistema valida que la fecha no sea anterior a la fecha actual y retorna un ID de producto único.

### HU-03 — Visualización de Precios Regionales

**Como** usuario, **quiero** consultar los precios promedio del Valle **para** negociar mejor.

- **Prioridad:** Should
- **Story Points:** 8

**Criterios de aceptación (BDD):**

- **Given** que existen 50 transacciones de café en las últimas 24 horas.
- **When** solicito el precio promedio del café.
- **Then** el sistema calcula la media aritmética y muestra el valor exacto en pesos colombianos.

### HU-04 — Filtro de Categorías

**Como** usuario, **quiero** filtrar los productos por categoría **para** encontrar rápidamente los productos que necesito.

- **Prioridad:** Must
- **Story Points:** 3

**Criterios de aceptación (BDD):**

- **Given** que estoy en el catálogo principal y existen productos de las categorías Frutas, Café y Verduras.
- **When** selecciono la categoría Frutas y, opcionalmente, un municipio en el panel de filtros.
- **Then** el sistema actualiza la vista y retorna `200 OK` con únicamente los productos de la categoría seleccionada.

### HU-05 — Contacto Directo

**Como** usuario, **quiero** contactar directamente al vendedor **para** realizar una solicitud, petición o consulta sobre sus productos.

- **Prioridad:** Should
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que el usuario accede a un formulario con sus datos de contacto, el vendedor y los productos de interés.
- **When** diligencia y envía el formulario de Contacto Directo.
- **Then** el sistema envía la petición o solicitud al contacto del vendedor.

### HU-06 — Registro de Compradores

**Como** comprador, **quiero** registrarme en la plataforma **para** comprar productos agrícolas.

- **Prioridad:** Must
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que el usuario ingresa a `/api/v1/auth/register`.
- **When** envía un JSON con nombre, correo, teléfono y contraseña válida.
- **Then** el sistema responde con `201 Created` y almacena la cuenta en PostgreSQL.

### HU-07 — Solicitar Transporte

**Como** comprador, **quiero** solicitar transporte para mi pedido **para** recibir los productos en mi ubicación.

- **Prioridad:** Must
- **Story Points:** 8

**Criterios de aceptación (BDD):**

- **Given** que existe un pedido confirmado.
- **When** selecciono dirección, municipio del Valle del Cauca y fecha de entrega.
- **Then** el sistema crea una solicitud de transporte con identificador único y estado Pendiente.

### HU-08 — Seguimiento del Envío

**Como** comprador, **quiero** consultar el estado del transporte **para** conocer cuándo llegará mi pedido.

- **Prioridad:** Should
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que existe un envío activo.
- **When** consulto el seguimiento del pedido.
- **Then** el sistema muestra el estado actualizado (Pendiente, En ruta o Entregado) con fecha y hora.

### HU-09 — Pago Seguro

**Como** comprador, **quiero** pagar mi pedido mediante un método seguro **para** completar la compra.

- **Prioridad:** Must
- **Story Points:** 13

**Criterios de aceptación (BDD):**

- **Given** que el pedido está listo para pagar.
- **When** selecciono un método de pago y autorizo la transacción.
- **Then** el sistema procesa el pago y responde con `200 OK` si la operación es exitosa.

### HU-10 — Confirmación de Pago

**Como** comprador, **quiero** recibir confirmación del pago **para** tener evidencia de la transacción.

- **Prioridad:** Should
- **Story Points:** 3

**Criterios de aceptación (BDD):**

- **Given** que el pago fue aprobado.
- **When** finaliza la transacción.
- **Then** el sistema genera un comprobante con número de transacción, fecha y valor pagado.

### HU-11 — Calificación del Vendedor

**Como** comprador, **quiero** calificar al vendedor después de recibir el pedido **para** compartir mi experiencia.

- **Prioridad:** Should
- **Story Points:** 3

**Criterios de aceptación (BDD):**

- **Given** que el pedido fue entregado.
- **When** asigno una calificación de 1 a 5 estrellas y escribo un comentario.
- **Then** el sistema guarda la reseña y actualiza el promedio del vendedor.

### HU-12 — Calificación del Comprador

**Como** agricultor, **quiero** calificar al comprador **para** fomentar transacciones confiables.

- **Prioridad:** Could
- **Story Points:** 3

**Criterios de aceptación (BDD):**

- **Given** que la compra fue completada.
- **When** asigno una calificación y un comentario.
- **Then** el sistema registra la valoración en el perfil del comprador.

### HU-13 — Historial de Compras

**Como** comprador, **quiero** consultar mi historial de compras **para** revisar pedidos anteriores.

- **Prioridad:** Should
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que el usuario tiene compras registradas.
- **When** accede a la sección Historial.
- **Then** el sistema muestra una lista con productos, vendedor, fecha y estado del pedido.

### HU-14 — Notificaciones del Pedido

**Como** usuario, **quiero** recibir notificaciones sobre cambios en el pedido **para** mantenerme informado.

- **Prioridad:** Could
- **Story Points:** 5

**Criterios de aceptación (BDD):**

- **Given** que existe un pedido activo.
- **When** cambia su estado a Confirmado, En ruta o Entregado.
- **Then** el sistema envía una notificación al usuario mediante la aplicación o correo electrónico.

### HU-15 — Guardar Productos Favoritos

**Como** comprador, **quiero** guardar productos como favoritos **para** encontrarlos fácilmente más adelante.

- **Prioridad:** Could
- **Story Points:** 3

**Criterios de aceptación (BDD):**

- **Given** que estoy visualizando un producto.
- **When** presiono el botón Agregar a Favoritos.
- **Then** el sistema almacena el producto en mi lista personal y responde con `200 OK`.

## Resumen de estimación

| Prioridad | Historias | Story Points |
| --- | --- | ---: |
| Must | HU-01, HU-02, HU-04, HU-06, HU-07, HU-09 | 39 |
| Should | HU-03, HU-05, HU-08, HU-10, HU-11, HU-13 | 29 |
| Could | HU-12, HU-14, HU-15 | 11 |
| **Total** | **15 historias** | **79** |

| Tarea                          | Story Points |
| -------------------------------- | -----------: |
| HU-01 – Registro de Agricultores |            5 |
| HU-02 – Publicación de Productos |            5 |
| HU-04 – Filtro de Categorías     |            3 |
| HU-06 – Registro de Compradores  |            5 |

