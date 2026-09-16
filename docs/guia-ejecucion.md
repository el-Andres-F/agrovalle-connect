# Guia de ejecucion — AgroValle Connect

Esta guia explica como preparar PostgreSQL, seleccionar Java 17, ejecutar el servidor y probar HU-01 y HU-02.

## Requisitos

- Java 17 instalado.
- PostgreSQL instalado y ejecutandose en el puerto `5432`.
- Base de datos creada en PostgreSQL con el nombre `agrovalle`.
- PowerShell abierto en la carpeta raiz del proyecto.

La conexion local se configura en `src/main/resources/application.properties`. No publiques contrasenas reales en GitHub.

## 1. Seleccionar Java 17

Ejecuta en PowerShell:

```powershell
. .\scripts\select-java.ps1 -Version 17
java -version
```

Maven tambien debe mostrar Java 17:

```powershell
.\mvnw.cmd -version
```

## 2. Ejecutar las pruebas

Para compilar y ejecutar las pruebas:

```powershell
.\mvnw.cmd test
```

Para crear el archivo ejecutable sin ejecutar las pruebas:

```powershell
.\mvnw.cmd package '-Dmaven.test.skip=true'
```

## 3. Iniciar el servidor

En una terminal PowerShell, ejecuta:

```powershell
. .\scripts\select-java.ps1 -Version 17
java -jar .\target\connect-0.0.1-SNAPSHOT.jar
```

Cuando aparezca `Tomcat started on port 8080`, el servidor estara disponible en:

```text
http://localhost:8080/
```

La ruta raiz muestra el estado de la API. Para detener el servidor presiona `Ctrl+C` en esa terminal.

## 4. Probar HU-01 — Registro de agricultores

Abre una segunda terminal PowerShell y ejecuta:

```powershell
$producer = Invoke-RestMethod `
  -Method Post `
  -Uri 'http://localhost:8080/api/v1/productores' `
  -ContentType 'application/json' `
  -Body '{"nombre":"Juan","ubicacion":"Palmira","cedula":"1234567890"}'

$producer
```

La respuesta debe incluir un `id`, por ejemplo:

```json
{
  "nombre": "Juan",
  "ubicacion": "Palmira",
  "cedula": "1234567890",
  "id": 1
}
```

Consultar los productores registrados:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/productores'
```

## 5. Probar HU-02 — Publicacion de productos

Usa el `id` del productor creado en HU-01. Si fue el primero, normalmente sera `1`:

```powershell
$product = Invoke-RestMethod `
  -Method Post `
  -Uri 'http://localhost:8080/api/v1/productos' `
  -ContentType 'application/json' `
  -Body '{"nombre":"Cafe","categoria":"CAFE","cantidad":10,"fechaCosecha":"2026-09-16","precio":12000,"productorId":1}'

$product
```

Consultar todos los productos:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/productos'
```

Filtrar por categoria:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/productos?categoria=CAFE'
```

La respuesta debe mostrar el producto publicado y su productor relacionado.

## Rutas disponibles

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| GET | `/` | Estado de la API |
| POST | `/api/v1/productores` | Registrar productor |
| GET | `/api/v1/productores` | Consultar productores |
| POST | `/api/v1/productos` | Publicar producto |
| GET | `/api/v1/productos` | Consultar productos |
| GET | `/api/v1/productos?categoria=CAFE` | Filtrar productos |

## Flujo rapido

```powershell
. .\scripts\select-java.ps1 -Version 17
.\mvnw.cmd package '-Dmaven.test.skip=true'
java -jar .\target\connect-0.0.1-SNAPSHOT.jar
```

El servidor debe permanecer ejecutandose en la primera terminal mientras realizas las peticiones desde la segunda.
