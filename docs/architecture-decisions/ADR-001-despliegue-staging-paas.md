# ADR-001: Adopción de Plataforma PaaS y GitHub Actions para Despliegue Continuo (CD) en Staging.

* **Estado:** Aprobado
* **Fecha:** 2026-11-12
* **Autores:** Célula de Desarrollo AgroValle Connect
* **Contexto Técnico:** Asignatura Ingeniería de Software II - UNIAJC

## Contexto y Problemática

El proyecto integrador AgroValle Connect requiere transicionar de la ejecución local en máquinas de desarrollo hacia un ambiente de Staging ejecutable en la nube. Se necesita una solución automatizable, integrada con el pipeline de GitHub Actions, que permita publicar incrementos funcionales de manera continua sin incurrir en costos de infraestructura ni en administración manual de servidores.

## Opciones Consideradas

1. **Despliegue Manual en Servidor VPS**: Ofrece control completo del sistema operativo, pero exige configuraciones manuales complejas, gestión de parches de seguridad y scripts de despliegue propensos al error humano.
2. **Despliegue Automatizado en Plataforma PaaS**: Permite integración mediante Webhooks de despliegue, aprovisionamiento gestionado de PostgreSQL y ejecución transparente del contenedor Spring Boot en Java 17.

## Decisión Adoptada

Se aprueba la **Opción 2: Plataforma PaaS** junto a **GitHub Actions**.
Cada integración validada en la rama `main` disparará automáticamente el pipeline de CD, el cual invocará el Webhook de despliegue en la PaaS para actualizar la aplicación ejecutándose con el perfil `application-staging.yml`.

## Consecuencias y Mantenibilidad

* **Consecuencias Positivas**:
  * Automatización completa del ciclo de entrega (*Continuous Deployment*).
  * Seguridad garantizada al inyectar contraseñas y llaves JWT desde GitHub Secrets.
  * Disponibilidad de una URL pública para validación del Product Owner y pruebas de integración.
* **Consecuencias Negativas / Limitaciones**:
  * La capa gratuita de PaaS puede experimentar un tiempo de latencia inicial (*Cold Start*) de aproximadamente 30 segundos tras periodos prolongados de inactividad.
