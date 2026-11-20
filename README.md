<div align="center">
  <h1> Sistema de Gestión de Ventas </h1>
  <p>
    <strong>Proyecto Final – Programación Orientada a Objetos</strong>
  </p>
  <p>
    Aplicación web para la gestión de clientes, productos y ventas,
    desarrollada con Java y Spring Boot.
  </p>
  <img src="https://img.shields.io/badge/Java-Spring%20Boot-orange" alt="Java Spring Boot">
  <img src="https://img.shields.io/badge/ORM-Spring%20Data%20JPA-green" alt="Spring Data JPA">
  <img src="https://img.shields.io/badge/Database-MySQL-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/Status-Development-yellow" alt="Development">
</div>

### Integrantes del GRUPO 5:

- Aravena, Marianella Belen
- Burgos, Zoe Dana Itati
- Castillo, Milagros Karen
- Garcia Martinez, Julieta Iris

## Descripción del proyecto

El proyecto consiste en el desarrollo de un sistema de gestión de ventas utilizando Java y Spring Boot.

La aplicación permite administrar clientes y productos, así como registrar y gestionar operaciones comerciales mediante dos modalidades: ventas de contado y ventas financiadas.

El sistema contempla la aplicación de descuentos mediante cupones, el cálculo de recargos financieros, la generación de cuotas y el control del stock de productos.

El desarrollo aplica conceptos de programación orientada a objetos, persistencia de datos, arquitectura por capas y separación de responsabilidades.

## Tecnologías utilizadas

### Desarrollo y persistencia
<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
</p>

### Herramientas
<p align="left">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Tools-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
</p>

### Control de versiones
<p align="left">
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" />
</p>

## Arquitectura del proyecto

La aplicación implementa una arquitectura por capas que permite separar las responsabilidades, organizar la lógica de negocio y facilitar el mantenimiento y la evolución del sistema.

### Organización por capas

| Capa | Responsabilidad |
|---|---|
| **Controller** | Recibe las solicitudes HTTP y devuelve las respuestas de la API. |
| **Service** | Implementa la lógica de negocio y las validaciones. |
| **Repository** | Gestiona el acceso y la persistencia de datos. |
| **Model** | Contiene las entidades y relaciones del dominio. |
| **DTO** | Define los objetos para recibir y responder solicitudes. |
| **Mapper** | Realiza la conversión entre entidades y DTOs. |
| **Exception** | Contiene las excepciones personalizadas y su tratamiento. |

### Organización de servicios

La lógica de negocio se distribuye en tres servicios principales:

- **ClienteService:** administración y validación de clientes.
- **ProductoService:** administración de productos y control de stock.
- **VentaService:** coordinación y gestión de las operaciones de venta.

## API REST

La aplicación expone endpoints REST para interactuar con las funcionalidades del sistema.

Los controladores reciben objetos de solicitud (**Request DTO**) y devuelven objetos de respuesta (**Response DTO**), evitando exponer directamente las entidades del modelo.

Las operaciones se organizan en los siguientes recursos:

- Clientes.
- Productos.
- Ventas.
- Ventas de contado.
- Ventas financiadas.
- Estado de cuenta.
- Cobro de cuotas.

### Documentación interactiva con Swagger

El proyecto incorpora Swagger para documentar y explorar los endpoints de la API.

Con la aplicación en ejecución, se puede acceder a la interfaz interactiva desde:

**[Swagger UI](http://localhost:8080/swagger-ui/index.html)**

Desde esta interfaz es posible consultar los endpoints, visualizar los parámetros de entrada, conocer los formatos de respuesta y ejecutar solicitudes HTTP.

## Validaciones y manejo de excepciones

El sistema incorpora excepciones personalizadas y un mecanismo centralizado de tratamiento de errores mediante `GlobalExceptionHandler`.

Esta implementación permite gestionar los errores de la aplicación y devolver respuestas HTTP apropiadas según el tipo de problema detectado.

Entre las validaciones contempladas se encuentran:

- Datos obligatorios y formatos de entrada.
- Existencia de clientes y productos.
- Disponibilidad de stock.
- Validez de cupones.
- Importes y porcentajes permitidos.
- Condiciones para registrar ventas.
- Condiciones para realizar el cobro de cuotas.

El manejo centralizado de excepciones permite mantener respuestas consistentes y evita duplicar lógica de tratamiento de errores en los controladores.

## Pruebas

El proyecto incorpora pruebas automatizadas utilizando JUnit y Spring Boot Test.

Las pruebas permiten verificar el comportamiento de los servicios, las reglas de negocio y las operaciones principales del sistema.

---

<div align="center">
  
  **Universidad Nacional de Jujuy - APU San Pedro – 2026**
  
</div>
