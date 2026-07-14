# Minimarket Plus API

Documentacion del proyecto backend Minimarket Plus, una API REST construida con Spring Boot para administrar productos, categorias, inventario, usuarios, roles, carrito de compras, ventas y detalles de venta.

Este archivo se creo como documentacion nueva y no reemplaza el `README.md` existente.

## Stack tecnico

- Java 17
- Spring Boot 3.4.1
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- H2 Database
- JWT con JJWT 0.12.6
- Springdoc OpenAPI / Swagger UI
- Maven Wrapper
- JaCoCo

## Requisitos

- JDK 17 o superior
- Maven instalado o el wrapper incluido en el proyecto

## Configuracion

La configuracion principal esta en `src/main/resources/application.properties`:

```properties
spring.application.name=minimarket
server.port=8081
jwt.secret=minimarket_plus123
jwt.expiration=36000000
```

La aplicacion se ejecuta en:

```text
http://localhost:8081
```

## Ejecutar el proyecto

En Windows:

```bash
mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

## Usuario inicial

Al iniciar la aplicacion, `DataInitializer` crea automaticamente un usuario administrador si no existe:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

El usuario queda asociado al rol:

```text
ROLE_ADMINISTRADOR
```

## Autenticacion JWT

Para obtener un token:

```http
POST /auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:

```json
{
  "token": "TOKEN_JWT"
}
```

Para consumir endpoints protegidos, enviar el token en el header:

```http
Authorization: Bearer TOKEN_JWT
```

## Swagger / OpenAPI

El proyecto incluye Springdoc OpenAPI. Una vez levantada la aplicacion, la documentacion interactiva esta disponible en:

```text
http://localhost:8081/swagger-ui/index.html
```

Tambien se puede consultar el JSON de OpenAPI en:

```text
http://localhost:8081/v3/api-docs
```

## Permisos por ruta

| Ruta | Acceso |
| --- | --- |
| `/auth/login` | Publico |
| `/public/**` | Publico |
| `/h2-console/**` | Publico |
| `/swagger-ui/**` | Publico |
| `/swagger-ui.html` | Publico |
| `/v3/api-docs/**` | Publico |
| `/api/usuarios/**` | `ROLE_ADMINISTRADOR` |
| `/api/roles/**` | `ROLE_ADMINISTRADOR` |
| `/api/productos/**` | `ROLE_CLIENTE`, `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/inventario/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/ventas/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/categorias/**` | `ROLE_ADMINISTRADOR` |
| `/api/carrito/**` | `ROLE_EMPLEADO`, `ROLE_CLIENTE`, `ROLE_ADMINISTRADOR` |
| `/api/detalle-ventas/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |

## Endpoints principales

| Modulo | Ruta base | Operaciones |
| --- | --- | --- |
| Autenticacion | `/auth` | `POST /login` |
| Prueba publica | `/public` | `GET /hola` |
| Usuarios | `/api/usuarios` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Roles | `/api/roles` | `GET`, `POST` |
| Productos | `/api/productos` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Categorias | `/api/categorias` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Inventario | `/api/inventario` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Carrito | `/api/carrito` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Ventas | `/api/ventas` | `GET`, `GET /{id}`, `POST` |
| Detalle de ventas | `/api/detalle-ventas` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |

## Ejemplos con curl

Login:

```bash
curl -X POST http://localhost:8081/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

Consultar productos:

```bash
curl http://localhost:8081/api/productos ^
  -H "Authorization: Bearer TOKEN_JWT"
```

Crear categoria:

```bash
curl -X POST http://localhost:8081/api/categorias ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer TOKEN_JWT" ^
  -d "{\"nombre\":\"Bebidas\"}"
```

Crear producto:

```bash
curl -X POST http://localhost:8081/api/productos ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer TOKEN_JWT" ^
  -d "{\"nombre\":\"Agua mineral\",\"precio\":1200,\"stock\":20,\"categoria\":{\"id\":1}}"
```

## Pruebas y cobertura

Ejecutar pruebas:

```bash
mvnw.cmd test
```

En Linux/macOS:

```bash
./mvnw test
```

El reporte de cobertura JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

## Estructura general

```text
src/main/java/com/minimarket
|-- config       Configuracion e inicializacion de datos
|-- controller   Controladores REST
|-- entity       Entidades JPA
|-- repository   Repositorios Spring Data
|-- security     Seguridad, JWT y filtros
`-- service      Interfaces e implementaciones de negocio
```

## Notas de desarrollo

- La base de datos es H2 en memoria por defecto.
- La API trabaja sin sesiones de servidor; usa `SessionCreationPolicy.STATELESS`.
- Las contrasenas se cifran con `BCryptPasswordEncoder`.
- Los endpoints protegidos requieren JWT valido.
- Swagger esta habilitado como documentacion interactiva para probar la API.
