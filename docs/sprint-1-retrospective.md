# Sprint 1 Retrospectiva

> **Instrucciones:** este documento se redacta en **tercera persona** ("el equipo", "los integrantes"). Completar con lo que el equipo discutió en la retrospectiva. Las observaciones marcadas como *sugeridas* provienen del análisis del repositorio y deben validarse o descartarse con el equipo.

## Datos

| Campo | Valor |
| --- | --- |
| Fecha | [COMPLETAR] |
| Facilitador | [COMPLETAR] |
| Participantes | Juan Chicangana, Andres Fuelagan, Juan Guerrero, Miguel Hurtado |

## ¿Qué salió bien?

- El equipo configuró el proyecto base con Java 17, Spring Boot, Checkstyle, Husky, Definition of Done y una guía de ejecución.
- [COMPLETAR con la discusión real]

## ¿Qué no salió bien?

- *(Sugerida)* La rama `develop` definida en la estrategia GitFlow no existe en el repositorio, y la mayoría de commits se hicieron directamente sobre `main`.
- *(Sugerida)* Los commits provienen casi en su totalidad de un solo integrante, lo que indica una distribución desigual del trabajo en Git.
- *(Sugerida)* El hook de pre-commit no bloqueaba commits: Checkstyle estaba configurado con `failOnViolation=false` y Husky no se activaba al clonar.
- *(Sugerida)* La simulación de planificación superaba la capacidad de 10 SP (sumaba 18 SP).
- [COMPLETAR con la discusión real]

## ¿Qué se puede mejorar?

- [COMPLETAR]

## Acciones de mejora para el Sprint 2

| # | Acción | Responsable | Fecha límite |
| --- | --- | --- | --- |
| 1 | Crear la rama `develop` y trabajar solo mediante ramas `feature/*` y Pull Requests. | [COMPLETAR] | [COMPLETAR] |
| 2 | Que cada integrante haga commits propios, asociados a una tarea del tablero. | [COMPLETAR] | [COMPLETAR] |
| 3 | Hacer que Checkstyle bloquee el commit ante infracciones. | [COMPLETAR] | [COMPLETAR] |
| 4 | Respetar los WIP Limits del tablero (In Progress ≤ 3, Code Review ≤ 2). | [COMPLETAR] | [COMPLETAR] |

## Compromiso del equipo

[COMPLETAR: redactado en tercera persona, por ejemplo "El equipo se compromete a…"]
