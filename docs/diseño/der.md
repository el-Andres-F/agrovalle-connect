# Diagrama Entidad–Relación (3FN)

```mermaid
erDiagram
    PRODUCTORES ||--o{ PRODUCTOS : publica
    COMPRADORES ||--o{ PEDIDOS : realiza
    PEDIDOS ||--|{ PEDIDO_DETALLES : contiene
    PRODUCTOS ||--o{ PEDIDO_DETALLES : aparece_en

    PRODUCTORES {
        bigint id PK
        varchar nombre
        varchar ubicacion
        varchar cedula UK
        varchar contacto
    }
    PRODUCTOS {
        bigint id PK
        varchar nombre
        varchar categoria
        int cantidad
        date fecha_cosecha
        double precio
        varchar ubicacion
        bigint productor_id FK
    }
    COMPRADORES {
        bigint id PK
        varchar nombre
        varchar apellido
        varchar correo UK
        varchar telefono
        varchar contrasena_hash
    }
    ADMIN {
        bigint id PK
        varchar usuario UK
        varchar contrasena_hash
        varchar nombre
        varchar apellido
        varchar correo
    }
    PEDIDOS {
        bigint id PK
        varchar referencia UK
        bigint comprador_id FK
        decimal total
        varchar estado
        timestamp fecha_creacion
        varchar direccion_entrega
    }
    PEDIDO_DETALLES {
        bigint id PK
        bigint pedido_id FK
        bigint producto_id FK
        int cantidad
        decimal precio_unitario
    }
```

La tabla administrativa es independiente de las relaciones de compra. `pedido_detalles` elimina grupos repetidos y conserva cantidad/precio histórico por línea; DDL equivalente en [`schema.sql`](schema.sql).
