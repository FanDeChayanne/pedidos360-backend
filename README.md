# Pedidos360 - Backend

Backend del proyecto **Pedidos360**, compuesto por tres microservicios desarrollados con **Spring Boot**:

* `ms-catalog` — Gestión del catálogo y stock
* `ms-orders` — Gestión de pedidos
* `ms-bff` — Backend For Frontend y punto de entrada para el frontend Angular

## Requisitos
* Java
* PostgreSQL
* Git


## Puertos
| Servicio     | Puerto | Función                       |
| ------------ | -----: | ----------------------------- |
| `ms-bff`     | `8080` | Punto de entrada del frontend |
| `ms-orders`  | `8081` | Gestión de pedidos            |
| `ms-catalog` | `8082` | Gestión de productos y stock  |

---

# 1. Levantar ms-catalog

Primero entrar a la carpeta:

```powershell
cd ms-catalog
```

Ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio quedará disponible en:

```text
http://localhost:8082
```

Este servicio administra:

* Productos
* Precios
* Stock
* Creación de productos
* Aumento de stock
* Disminución de stock

---

# 2. Levantar ms-orders

Abrir otra terminal y volver a la carpeta `backend`:

```powershell
cd ms-orders
```

Para ejecutarlo utilizando PostgreSQL local y conectándolo con `ms-catalog`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:postgresql://localhost:5432/pedidos360_orders --catalog.service.url=http://localhost:8082"
```

El servicio quedará disponible en:

```text
http://localhost:8081
```

Este servicio administra:

* Creación de pedidos
* Consulta de pedidos
* Actualización de pedidos
* Estados de los pedidos
* Cancelación de pedidos
* Comunicación con `ms-catalog` para controlar el stock

---

# 3. Levantar ms-bff

Abrir una tercera terminal y volver a la carpeta `backend`:

```powershell
cd ms-bff
```

Ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio quedará disponible en:

```text
http://localhost:8080
```

El BFF funciona como punto de entrada para el frontend y se comunica con los demás microservicios.

Sus conexiones son:

```text
ms-bff
  │
  ├── ms-orders
  │     └── http://localhost:8081
  │
  └── ms-catalog
        └── http://localhost:8082
```

---

# Orden recomendado de ejecución

Los servicios deben iniciarse en este orden:

```text
1. ms-catalog
       ↓
2. ms-orders
       ↓
3. ms-bff
```

Por lo tanto:

### Terminal 1

```powershell
cd ms-catalog
.\mvnw.cmd spring-boot:run
```

### Terminal 2

```powershell
cd ms-orders
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:postgresql://localhost:5432/pedidos360_orders --catalog.service.url=http://localhost:8082"
```

### Terminal 3

```powershell
cd ms-bff
.\mvnw.cmd spring-boot:run
```

---

# Comunicación con el frontend

El frontend Angular se comunica únicamente con el BFF:

```text
Angular
   │
   │ HTTP :8080
   ▼
ms-bff
   │
   ├──────────────► ms-orders :8081
   │
   └──────────────► ms-catalog :8082
```

El frontend no necesita comunicarse directamente con `ms-orders` ni con `ms-catalog`.

---

# Autenticación

El `ms-bff` utiliza **Microsoft Entra ID / OAuth 2.0 / JWT** para autenticar las solicitudes provenientes del frontend.

Las solicitudes protegidas deben incluir un token de acceso válido.

---

# Bases de datos

Los servicios utilizan PostgreSQL.

Base de datos de catálogo:

```text
pedidos360_catalog
```

Base de datos de pedidos:

```text
pedidos360_orders
```

La configuración de conexión se encuentra en los respectivos archivos:

```text
ms-catalog/src/main/resources/application.properties
ms-orders/src/main/resources/application.properties
```


# Notas

(cada tando el ms-orders llega a su limite de recursos cesa de funcionar, un reinicio lo arregla)

Cada microservicio posee su propio `pom.xml` y Maven Wrapper, por lo que no es necesario instalar Maven globalmente para ejecutarlos.

El proyecto está organizado dentro de un único repositorio backend para facilitar su administración y despliegue:

```text
backend/
├── ms-bff/
├── ms-orders/
├── ms-catalog/
└── README.md
```
