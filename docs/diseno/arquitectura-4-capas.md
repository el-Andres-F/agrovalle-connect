# Arquitectura MVC en cuatro capas

## Límites

1. **Presentación y API:** controladores `@RestController` reciben HTTP/JSON, aplican autorización de sesión y traducen respuestas HTTP. Los contratos de entrada/salida están en `dto`.
2. **Servicio y dominio:** `AutenticacionService`, `RegistroService`, `ProductoService`, `ProductorService`, `PagoPseService`, `PedidoService` y `AdminResumenService` implementan casos de uso y límites transaccionales. El checkout consume la abstracción `CheckoutStrategy`.
3. **Acceso a datos:** repositorios Spring Data (`JpaRepository`) encapsulan consultas y CRUD; los controladores REST delegan en servicios en los flujos de autenticación, productores, catálogo, pedidos, pagos y resumen.
4. **Persistencia:** PostgreSQL almacena entidades y relaciones JPA; `schema.sql` crea/ajusta las tablas y claves del modelo.

## Flujo representativo

`Navegador → ProductoController → ProductoService → ProductoRepository → PostgreSQL`

Para un checkout: `Navegador → PagoPseController → PagoPseService → CheckoutStrategy` y `PedidoRepository / ProductoRepository / CompradorRepository → PostgreSQL`.

El flujo de registro de productor valida nombre, ubicación y cédula dentro de `RegistroService`; la unicidad está respaldada por índice único en PostgreSQL. La política del catálogo de municipios se mantiene pendiente de confirmación del Product Owner.

## Despliegue

La vista de despliegue PlantUML es [despliegue.puml](despliegue.puml): cliente HTTP, servidor Java 17 LTS/Spring Boot-Tomcat en puerto 8080 y servidor PostgreSQL en 5432.
