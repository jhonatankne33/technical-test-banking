# Sistema de Gestión Bancaria - Microservicios

Sistema de microservicios para gestión de clientes, cuentas y movimientos bancarios. Implementa comunicación REST para validaciones críticas y eventos con RabbitMQ para operaciones asíncronas.

## Requisitos

- Java 17
- Docker y Docker Compose

## Estructura

- **client-service** (puerto 8081): Gestión de clientes
- **account-service** (puerto 8082): Gestión de cuentas y movimientos

## Ejecución

```bash
docker-compose up --build
```

Esto levanta todos los servicios:
- PostgreSQL para clientes (puerto 5432)
- PostgreSQL para cuentas (puerto 5433)
- RabbitMQ (puertos 5672 y 15672)
- client-service (puerto 8081)
- account-service (puerto 8082)

Consola RabbitMQ: http://localhost:15672 (guest/guest)

## Pruebas

Importa el archivo `Postman_Collection.json` en Postman para probar los endpoints.

Endpoints principales:
- GET http://localhost:8081/clientes - Listar clientes
- POST http://localhost:8081/clientes - Crear cliente
- GET http://localhost:8082/cuentas - Listar cuentas
- POST http://localhost:8082/movimientos - Registrar movimiento
- GET http://localhost:8082/reportes?fechaInicio=2024-01-01&fechaFin=2024-12-31&clienteId=1 - Reporte

Ejecutar pruebas unitarias:

```bash
mvn test -f client-service/pom.xml
mvn test -f account-service/pom.xml
```

## Arquitectura

El sistema usa dos microservicios independientes con bases de datos separadas:

**Comunicación sincrónica (REST)**:
- Validación de existencia de cliente al crear una cuenta
- Consulta de nombre de cliente para reportes

**Comunicación asincrónica (RabbitMQ)**:
- Cuando se elimina un cliente, se publica un evento
- El servicio de cuentas escucha el evento y desactiva todas las cuentas del cliente

**Decisiones de diseño**:
- Los movimientos no se pueden actualizar para mantener consistencia en el historial de saldos
- Una vez registrado un movimiento, no se puede modificar (solo agregar nuevos)
- El saldo actual se calcula a partir del último movimiento registrado

## Base de Datos

Las tablas y datos de prueba se cargan automáticamente al iniciar los contenedores.

Scripts SQL incluidos:
- `BaseDatos-client.sql` - Esquema y datos para clientes
- `BaseDatos-account.sql` - Esquema y datos para cuentas

## Notas

- Las tablas se crean usando los scripts SQL al iniciar los contenedores por primera vez
- JPA valida que las tablas existan y coincidan con las entidades
- RabbitMQ se configura automáticamente al iniciar
- Las colas se crean automáticamente si no existen
