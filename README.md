# AgroValle Connect

Plataforma digital para conectar a los productores del Valle del Cauca con mercados regionales y facilitar la comercializacion de productos agricolas.

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-repository-181717?logo=github&logoColor=white)

## Vision del producto

AgroValle Connect integra productores locales, compradores y la cadena de suministro en un solo espacio digital. La plataforma busca mejorar la visibilidad de la oferta agricola, facilitar acuerdos comerciales y optimizar el movimiento de productos desde el campo hasta los mercados regionales.


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

| Integrante |
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
src/main/java       Código fuente de la aplicación
src/test/java       Pruebas automatizadas
docs/               Documentación y Definition of Done
BACKLOG.md          Historias de usuario y prioridades
checkstyle.xml      Reglas de calidad de código
.husky/pre-commit   Validaciones antes de crear commits
```

## Documentacion

- [Product Backlog](BACKLOG.md)
- [Definition of Done](docs/dod.md)
- [Repositorio público en GitHub](https://github.com/el-Andres-F/agrovalle-connect)

# Definition of Done (DoD)
- [ ] El código compila sin errores.
- [ ] Se revisó el código por pares mediante un Pull Request.
- [ ] El código cumple con las reglas de checkstyle.xml.
- [ ] La historia de usuario cumple con los criterios de aceptación (Given-When-Then). 

**Firmado por:** El equipo de desarrollo.

</Step>
</Sequence>

