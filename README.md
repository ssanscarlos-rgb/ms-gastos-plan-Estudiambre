# ms-gastos-plan — EstudiAmbre

Microservicio de **Gastos y Plan** de EstudiAmbre (Fase II). Maneja los gastos del usuario, el plan de
presupuesto por categoría y el resumen del Panel. Cuando un gasto hace que una categoría llegue al 80% o al
100% de su presupuesto, publica un evento en Azure Queue Storage para que `ms-notificaciones` avise al usuario.

- **Tecnología:** Java 21, Spring Boot 3.4, arquitectura hexagonal
- **Base de datos:** Azure SQL (H2 en memoria para correr local)
- **Mensajería:** Azure Queue Storage (publica)
- **Dueño:** Omar

## Endpoints

Todas las rutas requieren `Authorization: Bearer <Google JWT>`. El usuario se toma del claim `sub`.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/v1/gastos?page=0&size=10&q=` | Lista paginada, más recientes primero, filtrable por descripción |
| POST | `/v1/gastos` | Crear `{descripcion, monto, categoria}` → 201 |
| PATCH | `/v1/gastos/{id}` | Editar solo los campos enviados |
| DELETE | `/v1/gastos/{id}` | Eliminar → 204 |
| GET | `/v1/plan` | `{meta, categorias:[{nombre, presupuesto, gastado}]}` |
| PATCH | `/v1/plan` | Cambiar `meta` y/o `categorias` (arreglo completo) |
| GET | `/v1/resumen` | `{disponible, gastado, meta, mensaje, ultimosMovimientos}` |
| GET | `/health` | Estado del servicio (sin autenticación) |
| GET | `/swagger` | Documentación interactiva (sin autenticación) |

- Gasto: `{id, descripcion, monto, categoria, fecha}`; `fecha` en epoch milisegundos, UTC.
- Paginación: `{content, page, size, totalElements, totalPages}`; página base 0; `size` máximo 50.
- Errores: `{status, message, correlationId}`. Campos desconocidos en el body → 400.
- `disponible` = suma de presupuestos del plan − total gastado. `ultimosMovimientos` = los 5 gastos más recientes.

### Evento en la cola

Al crear o editar un gasto, si la categoría **cruza** el 80% o el 100% de su presupuesto (solo al cruzar,
para no repetir el aviso):

```json
{"usuarioId":"<sub>","tipo":"PRESUPUESTO_80","categoria":"Comida","porcentaje":85,"fecha":1731000000000,"correlationId":"..."}
```

Por defecto va como texto JSON plano. Si `ms-notificaciones` lo espera en Base64, poner `QUEUE_BASE64=true`.
Si la cola falla, el gasto se guarda igual y el error queda en el log.

## Arquitectura hexagonal

```
src/main/java/com/estudiambre/gastosplan/
├── domain/                 Reglas del negocio. Java puro, sin Spring.
│   ├── model/              Gasto, Categoria, Plan, Pagina, Resumen, EventoPresupuesto
│   ├── service/            UmbralPresupuesto (¿se cruzó el 80% o el 100%?)
│   └── exception/
├── application/            Casos de uso. Java puro, solo conoce puertos.
│   ├── port/in/            GastosUseCase, PlanUseCase, ResumenUseCase   (lo que se puede hacer)
│   ├── port/out/           GastoRepositoryPort, PlanRepositoryPort, EventoPublisherPort   (lo que se necesita)
│   └── service/            GastoService, PlanService, ResumenService
└── infrastructure/         Detalles técnicos: adaptadores.
    ├── web/                Controladores REST, DTOs, errores, lectura del sub del JWT
    ├── persistence/        Entidades JPA + adaptadores de los puertos de repositorio (Azure SQL)
    ├── messaging/          Adaptador de EventoPublisherPort con Azure Queue Storage
    ├── security/           Filtro de certificado cliente y de X-Correlation-Id
    └── config/             Armado de beans y transacciones
```

- El dominio y la aplicación no importan nada de Spring, JPA ni Azure.
- Los servicios de aplicación se arman en `BeanConfig`. Las transacciones se agregan con decoradores
  (`TransaccionalGastosUseCase`) para que la capa de aplicación siga sin depender de Spring.
- **Ejemplo para la defensa:** el proyecto base del Lab 3 usaba MongoDB. Pasar a Azure SQL solo cambió
  `infrastructure/persistence`. Los casos de uso y el dominio no se tocaron.
- Las pruebas de `GastoServiceTest` corren los casos de uso con adaptadores en memoria, sin base de datos.

## ¿Por qué Azure SQL?

Los gastos, categorías y presupuestos son datos tabulares con una estructura fija. Lo que más se hace es
sumar y agrupar por categoría, filtrar por usuario y ordenar por fecha, que es justo lo que resuelve bien
una base relacional (`SUM ... GROUP BY`, índices por `usuario_id, fecha`). Además, crear o editar un gasto
y calcular el uso del presupuesto ocurre dentro de una transacción.

Tablas (las crea JPA al arrancar): `gastos`, `planes`, `plan_categorias`.

## Correr local

Requisitos: Java 21 e IntelliJ (trae Maven).

1. Abrir la carpeta en IntelliJ (**File → Open**) y esperar a que Maven descargue las dependencias.
2. Correr `GastosPlanApplication` (botón ▶).
3. Sin variables de entorno usa H2 en memoria, el certificado está desactivado y los eventos salen en el log.
4. Abrir <http://localhost:8080/swagger>.
5. Consola de H2 (para ver las tablas): <http://localhost:8080/h2-console>, JDBC URL
   `jdbc:h2:mem:gastosplan`, usuario `sa`, sin contraseña.

Por línea de comandos: `mvn spring-boot:run`. Pruebas: `mvn test`.

### Token de prueba

En local nadie valida la firma (eso lo hace APIM), así que sirve este token falso con `sub = usuario-prueba`:

```
Authorization: Bearer eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c3VhcmlvLXBydWViYSIsIm5hbWUiOiJQcnVlYmEifQ.firma
```

Ejemplo:

```bash
curl -X PATCH http://localhost:8080/v1/plan -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"meta":500,"categorias":[{"nombre":"Comida","presupuesto":100}]}'
curl -X POST http://localhost:8080/v1/gastos -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"descripcion":"Almuerzo","monto":85,"categoria":"Comida"}'
```

El segundo pasa el 80% de Comida: en la consola aparece el evento `PRESUPUESTO_80`.

## Variables de entorno

| Variable | Tipo en GitHub | Ejemplo / valor |
|---|---|---|
| `SQL_URL` | secret | `jdbc:sqlserver://<servidor>.database.windows.net:1433;database=<base>;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;` |
| `SQL_USER` | secret | usuario de Azure SQL |
| `SQL_PASSWORD` | secret | contraseña de Azure SQL |
| `STORAGE_CONNECTION_STRING` | secret | cadena de la Storage Account (cola) |
| `QUEUE_NAME` | variable | nombre de la cola acordado con ms-notificaciones (por defecto `eventos-presupuesto`) |
| `QUEUE_BASE64` | variable | `false` (o `true` si ms-notificaciones lo pide) |
| `CLIENT_CERT_REQUIRED` | variable | `true` en Azure |
| `CLIENT_CERT_THUMBPRINT` | secret | huella SHA-1 del certificado de APIM, sin `:` |
| `H2_CONSOLE_ENABLED` | variable | `false` en Azure |

Todas van también como **App settings** en el App Service (se leen al arrancar). Nunca en el código ni en commits.

## Despliegue

1. App Service con **Java SE 21**.
2. En el App Service: App settings con las variables de arriba; *Incoming client certificates* = **Require**.
3. En Azure SQL: *Networking* → permitir servicios de Azure (y la IP propia para probar local contra Azure).
4. En GitHub, Environment `test`: secret `AZUREAPPSERVICE_PUBLISHPROFILE` y variable `AZURE_WEBAPP_NAME`.
5. Push a la rama `test` → corre `.github/workflows/test_ms-gastos-plan.yml` (compila, prueba y publica).

Comprobación: `https://<app>.azurewebsites.net/health` responde `UP`; una llamada directa a `/v1/gastos`
responde **403**; la misma llamada por APIM responde **200**.

## Observabilidad

- `X-Correlation-Id`: se usa el que manda APIM o se genera uno; se devuelve en la respuesta, aparece en
  cada línea de log y viaja dentro del mensaje de la cola.
- Logs en JSON (formato ECS de Spring Boot).

## Pendientes de confirmar

- Con Jean: si el frontend espera otro cálculo para `disponible`, otro `mensaje` u otra cantidad de
  `ultimosMovimientos`.
- Con Andrés: nombre de la cola y si el mensaje va en JSON plano o Base64.
