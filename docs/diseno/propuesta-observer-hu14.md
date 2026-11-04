# Propuesta de diseño: patrón Observer para HU-14 (Notificaciones del Pedido)

**Estado:** propuesta documentada para el siguiente sprint. No está implementada en el código actual; el patrón comportamental implementado en el Sprint 2 es Strategy (`CheckoutStrategy`).

## Problema
Cuando cambia el estado de un pedido (por ejemplo, de `PAGO_PENDIENTE` a `PAGO_INICIADO`), el comprador y el productor deben recibir una notificación sin que `PedidoService` conozca los mecanismos de notificación.

## Diseño propuesto (Spring `ApplicationEventPublisher`)

| Elemento | Responsabilidad |
|---|---|
| `PedidoEstadoCambiadoEvent` | Evento de dominio inmutable (`pedidoId`, `estadoAnterior`, `estadoNuevo`). |
| `PedidoService` | Publica el evento con `ApplicationEventPublisher` después de persistir el cambio. |
| `NotificacionPedidoListener` | `@EventListener` que genera la notificación para comprador y productor. |

## Principios
- **SRP:** el servicio de pedidos no envía notificaciones.
- **OCP:** se agregan nuevos observadores (correo, panel) sin modificar `PedidoService`.
- **DIP:** el servicio depende de la abstracción `ApplicationEventPublisher`.

## Pruebas previstas (TDD)
1. RED: prueba unitaria con Mockito que verifica que `PedidoService` publica el evento al cambiar de estado.
2. GREEN: publicación mínima del evento.
3. REFACTOR: listener probado de forma aislada.
