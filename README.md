# Minimarket

API REST desarrollada con Spring Boot para la gestion de un minimarket. El proyecto permite administrar usuarios, roles, productos, categorias, inventario, carrito, ventas y detalles de venta, usando autenticacion JWT y control de acceso por roles.

## Tecnologias

- Java 17
- Spring Boot 3.4.1
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- JWT con `jjwt`
- Maven
- JaCoCo para cobertura de pruebas

## Requisitos

- JDK 17 o superior
- Maven, o usar el wrapper incluido:
  - Windows: `mvnw.cmd`
  - Linux/macOS: `./mvnw`

## Ejecucion

En Windows:

```bash
mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

La aplicacion queda disponible en:

```text
http://localhost:8081
```

El puerto se configura en `src/main/resources/application.properties`.

## Autenticacion

El sistema usa JWT. Para acceder a los endpoints protegidos primero se debe iniciar sesion:

```http
POST /auth/login
Content-Type: application/json
```

Credencial inicial creada automaticamente al iniciar la aplicacion:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta esperada:

```json
{
  "token": "jwt_generado"
}
```

Luego se debe enviar el token en las peticiones protegidas:

```http
Authorization: Bearer jwt_generado
```

## Roles y permisos

El proyecto maneja autorizacion por autoridades con formato `ROLE_*`.

| Ruta | Roles permitidos |
| --- | --- |
| `/auth/login` | Publico |
| `/public/**` | Publico |
| `/h2-console/**` | Publico |
| `/api/usuarios/**` | `ROLE_ADMINISTRADOR` |
| `/api/roles/**` | `ROLE_ADMINISTRADOR` |
| `/api/productos/**` | `ROLE_CLIENTE`, `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/inventario/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/ventas/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |
| `/api/categorias/**` | `ROLE_ADMINISTRADOR` |
| `/api/carrito/**` | `ROLE_EMPLEADO`, `ROLE_CLIENTE`, `ROLE_ADMINISTRADOR` |
| `/api/detalle-ventas/**` | `ROLE_EMPLEADO`, `ROLE_ADMINISTRADOR` |

## Endpoints principales

### Autenticacion

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `POST` | `/auth/login` | Inicia sesion y devuelve un token JWT |

### Prueba publica

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/public/hola` | Endpoint publico de prueba |

### Usuarios

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/usuarios` | Lista usuarios |
| `GET` | `/api/usuarios/{id}` | Obtiene un usuario por ID |
| `POST` | `/api/usuarios` | Crea un usuario |
| `PUT` | `/api/usuarios/{id}` | Actualiza un usuario |
| `DELETE` | `/api/usuarios/{id}` | Elimina un usuario |

### Roles

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/roles` | Lista roles |
| `POST` | `/api/roles` | Crea un rol |

### Productos

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/productos` | Lista productos |
| `GET` | `/api/productos/{id}` | Obtiene un producto por ID |
| `POST` | `/api/productos` | Crea un producto |
| `PUT` | `/api/productos/{id}` | Actualiza un producto |
| `DELETE` | `/api/productos/{id}` | Elimina un producto |

### Categorias

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/categorias` | Lista categorias |
| `GET` | `/api/categorias/{id}` | Obtiene una categoria por ID |
| `POST` | `/api/categorias` | Crea una categoria |
| `PUT` | `/api/categorias/{id}` | Actualiza una categoria |
| `DELETE` | `/api/categorias/{id}` | Elimina una categoria |

### Inventario

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/inventario` | Lista registros de inventario |
| `GET` | `/api/inventario/{id}` | Obtiene un registro por ID |
| `POST` | `/api/inventario` | Crea un registro |
| `PUT` | `/api/inventario/{id}` | Actualiza un registro |
| `DELETE` | `/api/inventario/{id}` | Elimina un registro |

### Carrito

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/carrito` | Lista elementos del carrito |
| `GET` | `/api/carrito/{id}` | Obtiene un elemento por ID |
| `POST` | `/api/carrito` | Agrega un elemento al carrito |
| `PUT` | `/api/carrito/{id}` | Actualiza un elemento del carrito |
| `DELETE` | `/api/carrito/{id}` | Elimina un elemento del carrito |

### Ventas

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/ventas` | Lista ventas |
| `GET` | `/api/ventas/{id}` | Obtiene una venta por ID |
| `POST` | `/api/ventas` | Registra una venta |

### Detalle de ventas

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/detalle-ventas` | Lista detalles de venta |
| `GET` | `/api/detalle-ventas/{id}` | Obtiene un detalle por ID |
| `POST` | `/api/detalle-ventas` | Crea un detalle de venta |
| `PUT` | `/api/detalle-ventas/{id}` | Actualiza un detalle de venta |
| `DELETE` | `/api/detalle-ventas/{id}` | Elimina un detalle de venta |

## Ejemplos de uso

### Login

```bash
curl -X POST http://localhost:8081/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

### Consultar productos

```bash
curl http://localhost:8081/api/productos ^
  -H "Authorization: Bearer TU_TOKEN"
```

### Crear una categoria

```bash
curl -X POST http://localhost:8081/api/categorias ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer TU_TOKEN" ^
  -d "{\"nombre\":\"Bebidas\"}"
```

### Crear un producto

```bash
curl -X POST http://localhost:8081/api/productos ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer TU_TOKEN" ^
  -d "{\"nombre\":\"Agua mineral\",\"precio\":1200,\"stock\":20,\"categoria\":{\"id\":1}}"
```

## Pruebas

Para ejecutar las pruebas:

```bash
mvnw.cmd test
```

En Linux/macOS:

```bash
./mvnw test
```

El proyecto genera reporte de cobertura con JaCoCo en:

```text
target/site/jacoco/index.html
```

## Estructura del proyecto

```text
src/main/java/com/minimarket
|-- config          # Inicializacion de datos
|-- controller      # Controladores REST
|-- entity          # Entidades JPA
|-- repository      # Repositorios Spring Data
|-- security        # Configuracion JWT y Spring Security
`-- service         # Interfaces e implementaciones de negocio
```

## Configuracion principal

Archivo: `src/main/resources/application.properties`

```properties
spring.application.name=minimarket
server.port=8081
jwt.secret=minimarket_plus123
jwt.expiration=36000000
```

## Notas

- La base de datos usada es H2 en memoria mediante la configuracion automatica de Spring Boot.
- Al iniciar la aplicacion se crea el usuario `admin` con rol `ROLE_ADMINISTRADOR` si no existe.
- Los endpoints protegidos requieren el header `Authorization: Bearer <token>`.
