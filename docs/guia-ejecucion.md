# Guia de ejecucion — AgroValle Connect

Esta guia explica como comprobar los requisitos, ejecutar el servidor y probar HU-01 y HU-02.

## Requisitos

- JDK 17 o superior. Esta aplicacion se comprobo ejecutandola con Java 26; el proyecto compila para Java 17.
- PostgreSQL instalado y ejecutandose en el puerto `5432`.
- Base de datos creada en PostgreSQL con el nombre `agrovalle`.
- PowerShell abierto en la carpeta raiz del proyecto (la carpeta que contiene `mvnw.cmd`).

La conexion local se configura en `src/main/resources/application.properties`. Asegurate de que el usuario y la contraseña configurados alli coincidan con tu PostgreSQL. 

## 1. Comprobar Java y la base de datos

En PowerShell, desde la raiz del proyecto, ejecuta:

```powershell
.\mvnw.cmd -version
```

En la salida, confirma que Maven usa Java 17 o superior. No es necesario ejecutar `npm` para arrancar esta aplicacion. Si quieres seleccionar una instalacion de Java detectada por el script del proyecto, puedes hacerlo en la misma ventana de PowerShell:

```powershell
. .\scripts\select-java.ps1 -Version 17
```

Usa esa seleccion solo si Java 17 esta instalado; despues vuelve a ejecutar el comando de comprobacion indicado arriba.

Confirma tambien que PostgreSQL este iniciado y que exista la base `agrovalle`.

## 2. Ejecutar las pruebas (opcional)

```powershell
.\mvnw.cmd test
```

## 3. Iniciar el servidor

El comando utilizado para ejecutar y comprobar esta aplicacion fue `spring-boot:run`. En PowerShell, desde la raiz del proyecto, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

La primera ejecucion puede tardar mientras Maven descarga dependencias. Cuando aparezca `Tomcat started on port 8080`, el servidor estara disponible en:

```text
http://localhost:8080/
```

La ruta raiz abre el panel web de AgroValle Connect. Desde alli puedes registrar productores y publicar productos; las listas se actualizan automaticamente. Deja esa terminal abierta mientras usas el panel. Para detener el servidor presiona `Ctrl+C` en esa terminal.

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
| GET | `/` | Panel web para productores y productos |
| GET | `/api/status` | Estado de la API |
| POST | `/api/v1/productores` | Registrar productor |
| GET | `/api/v1/productores` | Consultar productores |
| POST | `/api/v1/productos` | Publicar producto |
| GET | `/api/v1/productos` | Consultar productos |
| GET | `/api/v1/productos?categoria=CAFE` | Filtrar productos |

## Flujo rapido

```powershell
.\mvnw.cmd -version
.\mvnw.cmd spring-boot:run
```

El servidor debe permanecer ejecutandose en esa terminal mientras usas el panel en el navegador o realizas peticiones desde otra terminal.
