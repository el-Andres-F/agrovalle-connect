-- Ejecutar solo en bases existentes que todavía tengan pedidos.detalle NOT NULL.
-- Conserva el texto histórico y permite que las órdenes nuevas usen pedido_detalles.
ALTER TABLE pedidos ALTER COLUMN detalle DROP NOT NULL;
