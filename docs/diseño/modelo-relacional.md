# Modelo relacional — AgroValle Connect

**Motor objetivo:** PostgreSQL 16+ · **Codificación:** UTF-8 · **PK:** `BIGINT IDENTITY`.

## Relaciones y normalización (3FN)

- `productores` 1 — N `productos`: cada producto referencia a un productor mediante `productos.productor_id`.
- `compradores` 1 — N `pedidos`: cada pedido pertenece a una cuenta compradora.
- `pedidos` 1 — N `pedido_detalles`: cada fila representa un producto/cantidad/precio unitario de un pedido; no se almacena una lista delimitada en una columna.
- `productos` 1 — N `pedido_detalles`: los detalles referencian productos sin duplicar sus atributos descriptivos.
- `admin` contiene credenciales administrativas separadas de las cuentas compradoras.

Cada relación contiene atributos que dependen de la clave, de toda la clave y de ninguna otra columna no clave. Las claves de cédula, correo, usuario y referencia son únicas. La dirección de entrega pertenece al pedido; la cantidad y el precio histórico pertenecen al detalle del pedido.

## Diccionario de datos

| Tabla | Clave primaria | Atributos relevantes | Claves foráneas / restricciones |
|---|---|---|---|
| `productores` | `id` | `nombre`, `ubicacion`, `cedula`, `contacto` | `cedula` UNIQUE; nombre, ubicación y cédula NOT NULL |
| `productos` | `id` | `nombre`, `categoria`, `cantidad`, `fecha_cosecha`, `precio`, `ubicacion` | `productor_id` → `productores.id` |
| `compradores` | `id` | `nombre`, `apellido`, `correo`, `telefono`, `contrasena_hash` | `correo` UNIQUE |
| `admin` | `id` | `usuario`, `contrasena_hash`, `nombre`, `apellido`, `correo` | `usuario` UNIQUE |
| `pedidos` | `id` | `referencia`, `total`, `estado`, `fecha_creacion`, `direccion_entrega` | `comprador_id` → `compradores.id`; `referencia` UNIQUE |
| `pedido_detalles` | `id` | `cantidad`, `precio_unitario` | `pedido_id` → `pedidos.id`; `producto_id` → `productos.id` |

## Aplicación

El DDL ejecutable está en [`src/main/resources/schema.sql`](../../src/main/resources/schema.sql) y se inicializa al arrancar. Para instalaciones antiguas, ejecutar una vez [`migracion-pedidos-legado.sql`](migracion-pedidos-legado.sql) si `pedidos.detalle` aún es NOT NULL. El texto histórico se conserva, pero no puede reconstruirse automáticamente como líneas porque su formato concatenado no identifica inequívocamente los productos. Respaldar la base antes de migrar. El proyecto conserva `ddl-auto=update` para añadir diferencias JPA; el DDL y las anotaciones deben mantenerse sincronizados.
