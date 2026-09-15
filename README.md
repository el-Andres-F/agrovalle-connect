# AgroValle Connect

Plataforma digital para conectar a los productores del Valle del Cauca con mercados regionales y facilitar la comercializacion de productos agricolas.

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-repository-181717?logo=github&logoColor=white)

## Vision del producto

AgroValle Connect integra productores locales, compradores y la cadena de suministro en un solo espacio digital. La plataforma busca mejorar la visibilidad de la oferta agricola, facilitar acuerdos comerciales y optimizar el movimiento de productos desde el campo hasta los mercados regionales.

### Declaracion de la Vision del Producto

> Para **productores del Valle del Cauca**, que **necesitan vender directo** sin depender de intermediarios,
> **AgroValle Connect** es **una plataforma web construida en Java y Spring Boot**
> que **conecta oferta y demanda a precio justo** entre agricultores y compradores regionales.
> A diferencia de **los intermediarios tradicionales de la cadena agricola**,
> nuestro producto **garantiza trazabilidad del pedido y contratos de API transparentes** entre las partes.


## Como funciona

```mermaid
flowchart LR
    productor[Productor agricola] -->|Publica productos| plataforma((AgroValle Connect))
    comprador[Comprador regional] -->|Busca y solicita| plataforma
    plataforma -->|Conecta oferta y demanda| acuerdo[Acuerdo comercial]
    acuerdo --> logistica[Gestion de la cadena de suministro]
    logistica --> mercado[Mercado regional]

    classDef actor fill:#e8f3ed,stroke:#1f6f4a,color:#173b2b
    classDef core fill:#f4c95d,stroke:#a66b00,color:#3b2800
    classDef process fill:#e7eef7,stroke:#35618f,color:#1b344f
    class productor,comprador actor
    class plataforma core
    class acuerdo,logistica,mercado process
```

## Alcance inicial

- Registro y gestión de productores y compradores.
- Publicación y consulta de productos agrícolas.
- Solicitudes de compra y acuerdos comerciales.
- Seguimiento de la cadena de suministro.

## Equipo

| Integrantes |
| --- |
| Juan Chicangana |
| Andres Fuelagan |
| Juan Guerrero |
| Miguel Hurtado |

## Tecnologias

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- Git y GitHub

## Estrategia de ramas: GitFlow

Se utiliza GitFlow para separar el desarrollo de nuevas funcionalidades de las versiones estables:

- `main`: versiones estables y entregables.
- `develop`: integración de funcionalidades.
- `feature/*`: desarrollo de historias de usuario.
- `fix/*`: correcciones puntuales.

Los cambios se integran mediante Pull Requests revisados por otro integrante del equipo.

**Justificación:** el equipo optó por GitFlow (en lugar de Trunk-Based Development) porque el tamaño del equipo es pequeño, la dedicación es parcial (estudiantes) y el proyecto aún no cuenta con integración continua real. Aislar cada Historia de Usuario en una rama `feature/*` evita romper `develop` con cambios incompletos, y usar `develop` como rama de integración antes de `main` reduce los tiempos de espera entre integrantes: cada quien avanza en su propia rama y solo debe resolver conflictos al abrir el Pull Request, no en cada commit. Las ramas `feature/*` se mantienen deliberadamente cortas (una historia = una rama) para minimizar el riesgo de "Merge Hell" propio de ramas de larga duración.

```mermaid
gitGraph
    commit id: "Inicializacion del proyecto"
    branch develop
    checkout develop
    commit id: "Configuracion base"
    branch feature/HU-01
    checkout feature/HU-01
    commit id: "feat: implementar HU-01"
    checkout develop
    merge feature/HU-01
    checkout main
    merge develop tag: "v1.0.0"
```

## Estructura del proyecto

```text
src/main/java/.../modelo        Entidades JPA (Productor, Producto)
src/main/java/.../repositorio   Repositorios Spring Data JPA
src/main/java/.../controlador   Controladores REST
src/test/java                   Pruebas automatizadas
docs/                            Documentación y Definition of Done
BACKLOG.md                       Historias de usuario y prioridades
checkstyle.xml                   Reglas de calidad de código (Google Java Style)
.husky/pre-commit                Validaciones antes de crear commits (tests + Checkstyle)
```

## Documentacion

- [Product Backlog](BACKLOG.md)
- [Definition of Done](docs/dod.md)
- [Repositorio público en GitHub](https://github.com/el-Andres-F/agrovalle-connect)
