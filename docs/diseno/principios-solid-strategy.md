# Evidencia de SOLID y GoF Strategy

## SOLID

| Principio | Evidencia actual | Resultado buscado |
|---|---|---|
| SRP | `PagoPseController` adapta HTTP; `PagoPseService` valida carrito, calcula total y registra pedido; `WompiPseCheckoutStrategy` firma y genera URL. | Cada clase concentra una razón principal de cambio. |
| OCP | `PagoPseService` depende de `CheckoutStrategy`, no de Wompi. | Nuevos proveedores implementan la interfaz sin cambiar las reglas de cálculo/registro del pedido. |
| LSP | `WompiPseCheckoutStrategy` cumple el contrato `CheckoutStrategy`. | Una segunda estrategia debe respetar `disponible()` y producir una URL válida cuando está habilitada. |
| ISP | `CheckoutStrategy` expone únicamente disponibilidad y creación de URL de checkout. | Los consumidores no dependen de operaciones de proveedor que no usan. |
| DIP | El servicio de aplicación recibe `CheckoutStrategy` por constructor y los adaptadores REST reciben servicios. | Las políticas de aplicación dependen de abstracciones y no construyen clientes concretos de proveedor. |

## Patrón GoF: Strategy

`CheckoutStrategy` define el comportamiento intercambiable del checkout. `WompiPseCheckoutStrategy` implementa la firma de integridad y la URL hospedada de Wompi; `PagoPseService` consulta la disponibilidad y delega la generación del enlace. El cálculo de subtotal y la persistencia del pedido permanecen en la capa de aplicación, no en el controlador.

**Extensión pendiente:** si el producto incorpora otro proveedor de pago, se añadirá otra implementación y una política de selección; no se deben guardar llaves en el código fuente.

## Red-Green-Refactor / AAA

Las pruebas unitarias con Mockito separan Arrange (stubs y datos), Act (invocación del servicio) y Assert (resultado/errores/verificaciones). El historial local no constituye evidencia del orden temporal Red-Green-Refactor; la célula debe conservar capturas o commits de cada fase para acreditarlo formalmente.
