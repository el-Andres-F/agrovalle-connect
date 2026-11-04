# Tablero Kanban Sprint 2 — Estado de tarjetas

**Tablero:** https://github.com/users/el-Andres-F/projects/2
**Sprint 2:** 15 de octubre al 4 de noviembre de 2026 (sesiones de clase los jueves: 15, 22 y 29 de octubre).
**Columnas:** Backlog · To Do · En curso (In progress) · En revisión (In review) · Hecho (Done).
**Límites WIP:** En curso ≤ 3 · En revisión ≤ 2.

## Cómo hacer visibles los límites WIP

GitHub Projects no bloquea tarjetas por límite; el equipo los declara en la columna y los respeta:

1. Vista Board → menú (⋯) de la columna **En curso** → *Edit details*.
2. Nombre: `En curso (In progress) · WIP máx. 3`; descripción: `Límite de trabajo en progreso: 3 tarjetas`.
3. Repetir en **En revisión**: `En revisión (In review) · WIP máx. 2`.
4. Tomar la captura con las columnas y sus límites a la vista (Figura 3 del informe).

## Historias de usuario del Sprint 2

| Historia | Prioridad | SP | Columna | Responsable | Fecha de referencia |
|---|---|---:|---|---|---|
| HU-01 Registro de Agricultores | Must | 5 | Hecho | Andrés Camilo Fuelagan Cuastumal | Cierre 14/10/2026 |
| HU-06 Registro de Compradores | Must | 5 | Hecho | Juan David Chicangana Rodríguez | Cierre 14/10/2026 |
| HU-02 Publicación de Productos | Must | 5 | Hecho | Andrés Camilo Fuelagan Cuastumal | Cierre 22/10/2026 |
| HU-04 Filtro de Categorías | Must | 3 | Hecho | Juan José Guerrero Ospina | Cierre 22/10/2026 |
| HU-13 Historial de Compras | Should | 5 | Hecho | Miguel Ángel Hurtado Arias | Cierre 29/10/2026 |
| HU-09 Pago Seguro (PSE / Wompi) | Must | 13 | En revisión | Andrés Camilo Fuelagan Cuastumal | Inicio 22/10/2026 |
| HU-07 Solicitar Transporte | Must | 8 | To Do | Juan José Guerrero Ospina | Próximo sprint |
| HU-10 Confirmación de Pago | Should | 3 | To Do | Miguel Ángel Hurtado Arias | Próximo sprint |
| HU-14 Notificaciones del Pedido (patrón Observer) | Could | 5 | To Do | Juan David Chicangana Rodríguez | Próximo sprint; ver `docs/diseno/propuesta-observer-hu14.md` |
| HU-15 Guardar Productos Favoritos | Could | 3 | To Do | Juan David Chicangana Rodríguez | Próximo sprint (existe versión parcial en la interfaz) |

Las demás historias del Product Backlog (HU-03, HU-05, HU-08, HU-11 y HU-12) permanecen en **Backlog**.

## Tarjetas técnicas del Sprint 2

| Tarjeta | Columna | Responsable | Fecha de referencia | Evidencia |
|---|---|---|---|---|
| Modelo de datos: MER, DER 3FN, `schema.sql` y entidades JPA | Hecho | Juan José Guerrero Ospina | 14/10/2026 | `docs/diseno/` |
| Arquitectura MVC 4 capas, despliegue, secuencia, componentes y wireframes | Hecho | Juan José Guerrero Ospina | 28/10/2026 | `docs/diseno/` |
| Reescritura de HU-01 bajo ISO/IEC/IEEE 29148 frente a BDD | Hecho | Juan David Chicangana Rodríguez | 22/10/2026 | `docs/ieee29148-req01.md` |
| Refactorización SOLID y Strategy (`CheckoutStrategy`) | Hecho | Andrés Camilo Fuelagan Cuastumal | 04/11/2026 | PR #29 |
| Suite JUnit 5 + Mockito + MockMvc (53 pruebas) | Hecho | Andrés Camilo Fuelagan Cuastumal | 04/11/2026 | `src/test/java/` |
| Pipeline CI y umbral JaCoCo 60 % | Hecho | Miguel Ángel Hurtado Arias | 04/11/2026 | Run en verde, commit `89ae2ff` |
| Configurar Branch Protection en `main` | En curso | Miguel Ángel Hurtado Arias | Inicio 04/11/2026 | Settings → Branches |
| Completar bitácora, review y retrospectiva del Sprint 2 | En curso | Juan David Chicangana Rodríguez | Inicio 29/10/2026 | `docs/` |
| Reunir evidencias (CI, JaCoCo, tablero) y armar el PDF | En curso | Juan José Guerrero Ospina | Inicio 04/11/2026 | PDF de entrega |
| PR con la documentación del Sprint 2 | En revisión | Juan David Chicangana Rodríguez | 04/11/2026 | Pull Request |
| Confirmar con el Product Owner el catálogo de municipios de HU-01 | To Do | Juan José Guerrero Ospina | Próximo sprint | REQ-HU01-08 |

## Verificación de límites

- En curso: Branch Protection, documentación Sprint 2 y evidencias/PDF = **3** (límite 3).
- En revisión: HU-09 y PR de documentación = **2** (límite 2).
