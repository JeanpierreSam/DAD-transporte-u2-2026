# Ecosistema de Microservicios - Transporte Seguridad Miranda U2

## Descripción
Sistema de transporte con microservicios Java 21 + Spring Boot 3.2.5 + Spring Cloud 2023.0.1 con seguridad OAuth2.

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Requests                          │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │ ms-lib-api-     │ :9080
                    │ gateway         │ JWT Validation + Routing
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
    ┌──────▼──────┐  ┌──────▼──────┐  ┌───────▼───────┐
    │ ms-auth     │  │ ms-reserva  │  │  (Future MS)  │
    │ :9081       │  │ :9082       │  │               │
    │ OAuth2/JWKS │  │ PostgreSQL  │  │               │
    └─────────────┘  └─────────────┘  └───────────────┘
           │                 │
    ┌──────▼─────────────────▼──────
    │   ms-lib-registry-server      │ :9761 (Eureka)
    ───────────────────────────────┘
           ▲
    ┌──────┴────────────────────────┐
    │   ms-lib-config-server        │ :9888 (Config)
    └───────────────────────────────┘
```

---

## Puertos Asignados (Sin conflicto con otros proyectos)

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `ms-lib-config-server` | **9888** | Servidor de configuración centralizada |
| `ms-lib-registry-server` | **9761** | Eureka Server (descubrimiento de servicios) |
| `ms-lib-api-gateway` | **9080** | API Gateway + validación JWT |
| `ms-auth` | **9081** | OAuth2 Authorization Server + JWKS |
| `ms-reserva` | **9082** | Microservicio de gestión de reservas |
| PostgreSQL | **5442** | Base de datos (mapeado desde 5432 interno) |

> **Nota:** Los puertos 9888, 9761, 9080, 9081, 9082, 5442 fueron elegidos para evitar conflicto con otros proyectos que usan los puertos estándar 8888, 8761, 8080, 8084, 5432.

---

## Estructura del Proyecto

```
transporte-seguridad-miranda-u2/
├── pom.xml                          ← Parent POM agregador (multi-módulo)
├── .gitignore                       ← Excluye target/, .idea/, *.iml, *.log
├── docker-compose.yml               ← Orquestación con healthchecks
├── config-repo/                     ← Configuración externalizada
│   ├── shared-config.yml            ← Config compartida (Eureka, Actuator)
│   ├── ms-auth.yml                  ← Config específica de ms-auth
│   ├── ms-lib-api-gateway.yml       ← Rutas del Gateway
│   └── ms-reserva.yml               ← DataSource, JWT, JPA
├── postman-collection/
│   └── Transporte_Seguridad_Miranda_U2.postman_collection.json
├── ms-lib-config-server/            ← Módulo 1: Config Server
├── ms-lib-registry-server/          ← Módulo 2: Eureka Server
├── ms-lib-api-gateway/              ← Módulo 3: API Gateway
├── ms-auth/                         ← Módulo 4: OAuth2 Auth Server
└── ms-reserva/                      ← Módulo 5: Reservas (CRUD + Feign)
```

---

## 1. ms-lib-config-server (:9888)

### Stack
- `spring-cloud-config-server`
- `spring-boot-starter-actuator`

### Archivos clave
| Archivo | Descripción |
|---------|-------------|
| `ConfigServerApplication.java` | `@EnableConfigServer` |
| `application.yml` | Perfil `native`, lee de `file:./config-repo` |

### Configuración
```yaml
spring:
  cloud:
    config:
      server:
        native:
          search-locations: file:./config-repo
  profiles:
    active: native
```

---

## 2. ms-lib-registry-server (:9761)

### Stack
- `spring-cloud-starter-netflix-eureka-server`
- `spring-boot-starter-actuator`

### Archivos clave
| Archivo | Descripción |
|---------|-------------|
| `RegistryServerApplication.java` | `@EnableEurekaServer` |
| `application.yml` | Standalone, sin auto-registro |

### Configuración
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

---

## 3. ms-lib-api-gateway (:9080)

### Stack
- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-security`
- `spring-boot-starter-actuator`

### Archivos clave
| Archivo | Descripción |
|---------|-------------|
| `ApiGatewayApplication.java` | Application principal |
| `SecurityConfig.java` | Seguridad reactiva + OAuth2 Resource Server |
| `application.yml` | Importa config desde Config Server |

### Rutas configuradas (en `config-repo/ms-lib-api-gateway.yml`)
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ms-reserva
          uri: lb://ms-reserva
          predicates:
            - Path=/api/reservas/**
        - id: ms-auth-oauth2
          uri: lb://ms-auth
          predicates:
            - Path=/oauth2/**
        - id: ms-auth-jwks
          uri: lb://ms-auth
          predicates:
            - Path=/.well-known/**
```

### Validación JWT
- Valida tokens contra JWKS de ms-auth: `http://localhost:9081/.well-known/jwks.json`
- Rutas `/oauth2/**` y `/.well-known/**` son públicas
- Rutas `/api/**` requieren autenticación

---

## 4. ms-auth (:9081)

### Stack
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-security-oauth2-authorization-server`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-actuator`

### Archivos clave
| Archivo | Descripción |
|---------|-------------|
| `AuthApplication.java` | Application principal |
| `AuthorizationServerConfig.java` | Config OAuth2, RSA keys, clientes, usuarios |
| `JwtTokenCustomizerConfig.java` | Agrega claim `roles` al JWT |

### Datos de prueba en memoria

**Cliente OAuth2:**
- Client ID: `app-web-client`
- Client Secret: `secret`
- Grant types: `authorization_code`, `refresh_token`, `client_credentials`, `password`

**Usuarios:**
| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `admin` | `ROLE_ADMIN` |
| `user` | `user` | `ROLE_USER` |

### JWT Customizado
- Firma: RSA 2048 bits
- Claim personalizado: `roles` (array de strings sin prefijo `ROLE_`)
- Endpoint JWKS: `/.well-known/jwks.json`

### Fix aplicado
- **Línea 22 de `JwtTokenCustomizerConfig.java`:** Cambiado `Collection<GrantedAuthority>` → `Collection<? extends GrantedAuthority>` para resolver error de compilación por invarianza de genéricos.

---

## 5. ms-reserva (:9082)

### Stack
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-openfeign`
- `spring-cloud-starter-config`
- `spring-boot-starter-actuator`
- `postgresql` (runtime)
- `h2` (runtime, perfil default)
- `lombok` (optional)

### Archivos clave
| Archivo | Descripción |
|---------|-------------|
| `ReservaApplication.java` | `@EnableFeignClients` |
| `model/Reserva.java` | Entity JPA con índices |
| `repository/ReservaRepository.java` | JpaRepository |
| `service/ReservaService.java` | Lógica + validación Feign |
| `controller/ReservaController.java` | REST API con `@PreAuthorize` |
| `config/SecurityConfig.java` | `@EnableMethodSecurity` + OAuth2 |
| `dto/ReservaRequestDto.java` | DTO de entrada (Lombok `@Data`) |
| `dto/ReservaResponseDto.java` | DTO de salida con campos enriquecidos (Lombok `@Data`) |

### Modelo de Datos - `Reserva`
```java
@Entity
@Table(name = "reserva", indexes = {
    @Index(name = "idx_fecha_cli", columnList = "fecha_reser, cod_cli"),
    @Index(name = "idx_prog_dest", columnList = "id_prog, cod_dest")
})
public class Reserva {
    @Id
    @Column(name = "nro_reser", length = 8)
    private String nroReser;

    @Column(name = "fecha_reser")
    private LocalDate fechaReser;

    @Column(name = "hora_reser")
    private LocalTime horaReser;

    @Column(name = "cod_cli", length = 5)
    private String codCli;          // FK lógica (String)

    @Column(name = "id_prog")
    private Integer idProg;         // FK lógica (Integer)

    @Column(name = "cod_dest", length = 4)
    private String codDest;         // FK lógica (String)
}
```

> **Sin `@ManyToOne` ni FKs físicas.** Claves foráneas son campos simples (`String`/`Integer`).

### DTOs (Lombok `@Data`)

**ReservaRequestDto** (entrada):
```java
@Data
public class ReservaRequestDto {
    private String nroReser;
    private LocalDate fechaReser;
    private LocalTime horaReser;
    private String codCli;
    private Integer idProg;
    private String codDest;
}
```

**ReservaResponseDto** (salida con campos enriquecidos):
```java
@Data
public class ReservaResponseDto {
    private String nroReser;
    private LocalDate fechaReser;
    private LocalTime horaReser;
    private String codCli;
    private String nombreCliente;              // Enriquecido vía Feign
    private Integer idProg;
    private String descripcionProgramacion;    // Enriquecido vía Feign
    private String codDest;
    private String nombreDestino;              // Enriquecido vía Feign
}
```

### Feign Clients (interfaces sin implementación)
| Cliente | Servicio destino | Método |
|---------|-----------------|--------|
| `ClienteFeignClient` | `ms-cliente` | `GET /api/clientes/{codCli}` |
| `ProgramacionFeignClient` | `ms-programacion` | `GET /api/programaciones/{idProg}` |
| `BusFeignClient` | `ms-bus` | `GET /api/buses/{idBus}` |
| `AsientoFeignClient` | `ms-asiento` | `GET /api/asientos/{idAsiento}` |
| `DestinoFeignClient` | `ms-destino` | `GET /api/destinos/{codDest}` |

### API REST
| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| `GET` | `/api/reservas` | USER, ADMIN | Lista todas las reservas (DTOs enriquecidos) |
| `POST` | `/api/reservas` | USER, ADMIN | Crea reserva + valida referencias vía Feign |
| `DELETE` | `/api/reservas/{nroReser}` | ADMIN | Elimina reserva |

### Perfiles de Base de Datos
| Perfil | DataSource | Uso |
|--------|-----------|-----|
| `default` | H2 en memoria (`jdbc:h2:mem:reserva`) | Desarrollo local |
| `postgresql` | PostgreSQL (`jdbc:postgresql://localhost:5442/reserva_db`) | Producción / Docker |

### Fix aplicado
- **DTOs con Lombok `@Data`:** Reemplazados ~150 líneas de getters/setters manuales por anotaciones `@Data` de Lombok.
- **pom.xml:** Agregado `<excludes>` para Lombok en `spring-boot-maven-plugin`.

---

## Docker Compose

### Servicios
| Servicio | Puerto Host | Puerto Container | Healthcheck |
|----------|-------------|------------------|-------------|
| `postgres-reserva` | 5442 | 5432 | `pg_isready` |
| `config-server` | 9888 | 9888 | `/actuator/health` |
| `registry-server` | 9761 | 9761 | `/actuator/health` |
| `api-gateway` | 9080 | 9080 | `/actuator/health` |
| `ms-auth` | 9081 | 9081 | `/actuator/health` |
| `ms-reserva` | 9082 | 9082 | `/actuator/health` |

### Características
- **Healthchecks** en todos los servicios para orden de inicio correcto
- **Volumen** `./config-repo:/app/config-repo` montado en config-server
- **Red aislada** `transporte-net`
- **Dependencias condicionales:** ms-reserva espera a PostgreSQL (healthy), registry y auth

### Comando
```bash
docker-compose up --build
```

---

## Configuración Externalizada (config-repo/)

| Archivo | Servicio | Contenido |
|---------|----------|-----------|
| `shared-config.yml` | Todos | Eureka client, Actuator endpoints |
| `ms-auth.yml` | ms-auth | Puerto 9081 |
| `ms-lib-api-gateway.yml` | ms-lib-api-gateway | Puerto 9080, rutas Gateway, JWKS URI |
| `ms-reserva.yml` | ms-reserva | Puerto 9082, DataSource PostgreSQL, JWT, JPA |

### Fix aplicado
- **Config Server:** Cambiado de `classpath:/config` a `file:./config-repo` para configuración externalizada y portable.
- **Puerto PostgreSQL:** Cambiado de `5432` a `5442` en todos los archivos para evitar conflicto con `postgres-auth` de otros proyectos.

---

## Postman Collection

### Ubicación
`postman-collection/Transporte_Seguridad_Miranda_U2.postman_collection.json`

### Estructura
```
Transporte Seguridad Miranda U2
├── 01 - Infraestructura          (6 requests: health, config, eureka)
├── 02 - Auth (OAuth2)            (6 requests: JWKS, tokens, introspect)
├── 03 - Reservas (CRUD)          (4 requests: GET, POST x2, DELETE)
├── 04 - Gateway                  (2 requests: health, sin auth)
── 05 - Pruebas de Seguridad     (4 requests: 401, 403 scenarios)
```

### Variables de colección
| Variable | Valor | Uso |
|----------|-------|-----|
| `gateway_url` | `http://localhost:9080` | API Gateway |
| `auth_url` | `http://localhost:9081` | Auth Server |
| `config_url` | `http://localhost:9888` | Config Server |
| `registry_url` | `http://localhost:9761` | Eureka Server |
| `reserva_url` | `http://localhost:9082` | ms-reserva directo |
| `access_token` | (auto) | Se guarda automáticamente tras obtener token |
| `client_id` | `app-web-client` | OAuth2 client |
| `client_secret` | `secret` | OAuth2 secret |

### Scripts automáticos
- Los requests de **Token - Password Grant** guardan el `access_token` automáticamente en la variable de colección.
- El request **DELETE sin rol ADMIN (403)** verifica que se reciba 403.

---

## Flujo de Prueba Recomendado

1. **Levantar servicios:** `docker-compose up --build`
2. **Importar colección** en Postman
3. **Obtener token ADMIN:** `02 - Auth → Token - Password Grant (admin)`
4. **Listar reservas:** `03 - Reservas → GET - Listar Reservas`
5. **Crear reserva:** `03 - Reservas → POST - Crear Reserva`
6. **Verificar seguridad sin token:** `05 - Seguridad → Acceso sin token (401)`
7. **Cambiar a token USER:** `02 - Auth → Token - Password Grant (user)`
8. **Verificar restricción ADMIN:** `05 - Seguridad → DELETE sin rol ADMIN (403)`

---

## Historial de Cambios

| Fecha | Cambio | Archivos afectados |
|-------|--------|-------------------|
| 2026-06-15 | Creación inicial de 5 microservicios | Todos |
| 2026-06-15 | Parent POM agregador con `<modules>` | `pom.xml` (raíz) |
| 2026-06-15 | `.gitignore` para excluir build artifacts | `.gitignore` |
| 2026-06-15 | `config-repo/` externalizado con ymls por servicio | `config-repo/*` |
| 2026-06-15 | Config Server apunta a `file:./config-repo` | `ms-lib-config-server/application.yml` |
| 2026-06-15 | DTOs `ReservaRequestDto` y `ReservaResponseDto` | `ms-reserva/dto/*` |
| 2026-06-15 | Controller y Service actualizados para usar DTOs | `ReservaController.java`, `ReservaService.java` |
| 2026-06-15 | Actuator en todos los microservicios | Todos los `pom.xml` |
| 2026-06-15 | Healthchecks en docker-compose | `docker-compose.yml` |
| 2026-06-15 | Lombok `@Data` en DTOs (reemplaza getters/setters) | `ReservaRequestDto.java`, `ReservaResponseDto.java` |
| 2026-06-15 | Fix: `Collection<? extends GrantedAuthority>` | `JwtTokenCustomizerConfig.java:22` |
| 2026-06-15 | Fix: Puerto PostgreSQL 5432 → 5442 | `docker-compose.yml`, `config-repo/ms-reserva.yml`, `application.yml` (perfil postgresql) |
| 2026-06-15 | Postman Collection con 5 carpetas y 22 requests | `postman-collection/*` |

---

## Referencias

- **Repo de referencia:** https://github.com/kakatsumi/transporte-examen-microservicio
- **Spring Boot:** 3.2.5
- **Spring Cloud:** 2023.0.1
- **Java:** 21
- **PostgreSQL:** 16-alpine
