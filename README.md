# AgroValle Connect

## Visión del Producto
AgroValle Connect es una plataforma agrícola diseñada para el ecosistema del Valle del Cauca. Su objetivo es integrar tecnologías de información para conectar a productores locales con mercados regionales, optimizando la cadena de suministro mediante una arquitectura robusta y escalable.

## Integrantes del Equipo
* Andres Fuelagan ()
* [Nombre del integrante 2]
* [Nombre del integrante 3]
* [Nombre del integrante 3]


## Estrategia de Ramas (GitFlow)
Utilizaremos GitFlow para mantener un desarrollo organizado, separando las nuevas características de la rama principal de producción.

3.Llena el README.md con el diagrama Mermaid:Abre el archivo README.md y pega esta plantilla. Ya incluye la visión del producto, el espacio para tu equipo y el diagrama de estrategia de ramas en código Mermaid:Markdown# AgroValle Connect

## Visión del Producto
AgroValle Connect es una plataforma agrícola diseñada para el ecosistema del Valle del Cauca. Su objetivo es integrar tecnologías de información para conectar a productores locales con mercados regionales, optimizando la cadena de suministro mediante una arquitectura robusta y escalable.

## Integrantes del Equipo
* Andres Fuelagan (Representante)
* [Nombre del integrante 2]
* [Nombre del integrante 3]

## Estrategia de Ramas (GitFlow)
Utilizaremos GitFlow para mantener un desarrollo organizado, separando las nuevas características de la rama principal de producción.

mermaid
gitGraph
3.Llena el README.md con el diagrama Mermaid:Abre el archivo README.md y pega esta plantilla. Ya incluye la visión del producto, el espacio para tu equipo y el diagrama de estrategia de ramas en código Mermaid:Markdown# AgroValle Connect

## Visión del Producto
AgroValle Connect es una plataforma agrícola diseñada para el ecosistema del Valle del Cauca. Su objetivo es integrar tecnologías de información para conectar a productores locales con mercados regionales, optimizando la cadena de suministro mediante una arquitectura robusta y escalable.

## Integrantes del Equipo
* Andres Fuelagan (Representante)
* [Nombre del integrante 2]
* [Nombre del integrante 3]

## Estrategia de Ramas (GitFlow)
Utilizaremos GitFlow para mantener un desarrollo organizado, separando las nuevas características de la rama principal de producción.

mermaid 
gitGraph
commit id: "Inicializar"
branch develop
checkout develop 
commit id: "Configuración base" 
branch feature/HU-01 
checkout feature/HU-01
commit id: "feat: registro agricultor"
checkout develop
merge feature/HU-01
checkout main
merge develop tag: "v1.0" 

*(Nota: los tres acentos graves ` al final cierran el bloque de código).*
</Step>
<Step title="Llena el BACKLOG.md y el dod.md">
*   **En el archivo `BACKLOG.md`:** Pega las 15 historias de usuario que generamos anteriormente (desde la HU-01 hasta la HU-15), las cuales ya están en formato BDD y categorizadas con MoSCoW. Solo tendrías que agregarle los "Story Points" (ej. 1, 2, 3, 5, 8) al lado del título de cada historia.
*   **En el archivo `docs/dod.md`:** Pega un checklist de calidad como este: 

markdown
# Definition of Done (DoD)
- [ ] El código compila sin errores.
- [ ] Se revisó el código por pares mediante un Pull Request.
- [ ] El código cumple con las reglas de checkstyle.xml.
- [ ] La historia de usuario cumple con los criterios de aceptación (Given-When-Then). 

**Firmado por:** El equipo de desarrollo.

</Step>
</Sequence>