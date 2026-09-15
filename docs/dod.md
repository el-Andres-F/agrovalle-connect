# Definition of Done — AgroValle Connect

## Objetivo

La Definition of Done (DoD) es el contrato técnico innegociable que garantiza la integridad de cada incremento funcional, alineado con los atributos de calidad de la norma **ISO/IEC 25010** (Mantenibilidad, Adecuación Funcional y Fiabilidad).

## Checklist de cumplimiento obligatorio

Una Historia de Usuario se considera terminada cuando cumple todos los siguientes criterios:

- [ ] **Build Local:** el proyecto compila sin errores en el entorno local (`./mvnw clean package`).
- [ ] **Linter Pass (Modularidad/Estilo):** el código cumple las reglas estáticas definidas en `checkstyle.xml` (basado en Google Java Style), sin advertencias.
- [ ] **Functional Correctness:** el 100 % de las pruebas unitarias existentes pasan con éxito (`./mvnw test`).
- [ ] **Peer Review:** el Pull Request fue revisado y aprobado por al menos un par del equipo antes del merge.
- [ ] **Documentation:** el `README.md` y la documentación técnica de `/docs` están actualizados.
- [ ] **Commits:** el historial sigue estrictamente la convención de Conventional Commits (`feat`, `fix`, `docs`, `chore`, etc.).
- [ ] **Automatización:** los hooks de Husky (`.husky/pre-commit`) están activos y bloquean el commit si fallan el linter o las pruebas.
- [ ] Los criterios de aceptación BDD (Given-When-Then) de la Historia de Usuario fueron cumplidos.
- [ ] La funcionalidad fue integrada correctamente a la rama correspondiente.
- [ ] No existen errores conocidos que impidan considerar terminada la Historia de Usuario.

## Criterio final

Una Historia de Usuario solamente podrá considerarse **Done** cuando todos los criterios anteriores hayan sido cumplidos.

## Firma del equipo

| Integrante | Firma |
| --- | --- |
| Juan Chicangana | __________________________ |
| Andres Fuelagan | __________________________ |
| Juan Guerrero | __________________________ |
| Miguel Hurtado | __________________________ |

**Fecha:** 15 de septiembre de 2026
