# Evaluacion-Modular-6---Sistema-de-Gestion-de-Proyectos-y-Tareas
Repositorio de Final del Modulo 6 del Curso Desarrollador de Aplicaciones Full Stack Java Trainee v2.0 0033-1 de Kibernum.

# Modular6 — API REST con Spring Boot y JWT

Proyecto educativo que integra los componentes principales de una aplicación backend moderna: **Spring Boot**, **Spring Security**, **JPA/Hibernate**, **JWT** y **MySQL**. El objetivo es aprender cómo cada capa se conecta con las demás para construir una API segura y escalable.

---

## Tabla de contenidos

1. [¿Qué hace esta aplicación?](#1-qué-hace-esta-aplicación)
2. [Tecnologías utilizadas](#2-tecnologías-utilizadas)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Capa de Modelos (Entities)](#4-capa-de-modelos-entities)
5. [Capa de Repositorios](#5-capa-de-repositorios)
6. [Capa de Servicios](#6-capa-de-servicios)
7. [Capa de Controladores (Endpoints)](#7-capa-de-controladores-endpoints)
8. [DTOs — Data Transfer Objects](#8-dtos--data-transfer-objects)
9. [Seguridad con JWT](#9-seguridad-con-jwt)
10. [Flujo completo de autenticación](#10-flujo-completo-de-autenticación)
11. [Cómo levantar el proyecto](#11-cómo-levantar-el-proyecto)
12. [Endpoints de la API](#12-endpoints-de-la-api)
13. [Errores comunes y su causa](#13-errores-comunes-y-su-causa)

---

## 1. ¿Qué hace esta aplicación?

Es una API REST para gestionar **proyectos** y sus **tareas**. Tiene sistema de usuarios con dos roles:

| Rol | Permisos |
|-----|----------|
| `ADMIN` | Crear y listar proyectos y tareas |
| `USER` | Solo listar proyectos y tareas |

Todas las rutas (excepto registro y login) requieren un **token JWT** en el header de la petición.

---

## 2. Tecnologías utilizadas

| Tecnología | Versión | Para qué se usa |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.13 | Framework base de la aplicación |
| Spring Security | (incluido) | Autenticación y autorización |
| Spring Data JPA | (incluido) | Acceso a base de datos con ORM |
| Hibernate | (incluido) | Implementación de JPA (genera el SQL) |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| MySQL | 8.4 | Base de datos relacional |
| Docker Compose | - | Levanta MySQL en un contenedor |
| BCrypt | (incluido) | Encriptación de contraseñas |
| Maven | - | Gestión de dependencias y build |

---

## 3. Estructura del proyecto

```
modular6/
├── src/main/java/cl/playground/modular6/
│   ├── Modular6Application.java        ← Punto de entrada
│   ├── configuration/
│   │   └── SecurityConfig.java         ← Configuración de Spring Security
│   ├── controller/                     ← Reciben peticiones HTTP
│   │   ├── AuthController.java
│   │   ├── ProyectoController.java
│   │   └── TareaController.java
│   ├── dto/                            ← Objetos de transferencia de datos
│   │   ├── CreateProyectoDTO.java
│   │   ├── ListProyectoDTO.java
│   │   ├── CreateTareaDTO.java
│   │   ├── ListTareaDTO.java
│   │   ├── RegisterDTO.java
│   │   └── LoginDTO.java
│   ├── model/                          ← Entidades de base de datos
│   │   ├── Usuario.java
│   │   ├── Rol.java
│   │   ├── Proyecto.java
│   │   └── Tarea.java
│   ├── repository/                     ← Acceso a base de datos
│   │   ├── ProyectoRepository.java
│   │   ├── TareaRepository.java
│   │   └── UsuarioRepository.java
│   ├── service/                        ← Lógica de negocio
│   │   ├── ProyectoService.java
│   │   ├── TareaService.java
│   │   └── impl/
│   │       ├── ProyectoServiceImpl.java
│   │       ├── TareaServiceImpl.java
│   │       └── UserDetailServiceImpl.java
│   └── utils/                          ← Utilidades de JWT
│       ├── JwtUtil.java
│       └── JwtFilter.java
└── src/main/resources/
    └── application.properties          ← Configuración de la app
```

> **Concepto clave — Arquitectura en capas:** Cada capa solo habla con la capa inmediatamente debajo. El Controller llama al Service, el Service llama al Repository, el Repository habla con la base de datos. Nunca un Controller accede directamente a la base de datos.

---

## 4. Capa de Modelos (Entities)

Las entidades son clases Java que **mapean directamente a tablas** en la base de datos. Hibernate lee las anotaciones `@Entity`, `@Table`, `@Column` y genera el SQL automáticamente.

### `Rol.java` — Enum

```java
public enum Rol {
    ADMIN,
    USER
}
```

Un enum simple que define los dos roles del sistema. Se almacena como texto (`EnumType.STRING`) en la columna `rol` de la tabla `usuarios`.

---

### `Usuario.java` — Tabla `usuarios`

```
id | email | password | nombre | rol
```

Lo más importante de esta clase es que **implementa `UserDetails`**, la interfaz de Spring Security que define cómo se ve un usuario autenticado:

```java
public class Usuario implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le dice a Spring Security qué rol tiene este usuario
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return email; // usamos el email como nombre de usuario
    }
}
```

> **Concepto clave:** Al implementar `UserDetails`, la clase `Usuario` se convierte en el objeto que Spring Security utiliza internamente para representar a un usuario autenticado. Spring Security buscará los roles en `getAuthorities()` para decidir si puede acceder a un endpoint.

---

### `Proyecto.java` — Tabla `proyectos`

```
id | nombre | descripcion
```

Tiene una relación **uno a muchos** con `Tarea`:

```java
@OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Tarea> tareas = new ArrayList<>();
```

> **Concepto clave — Cascade:** `CascadeType.ALL` significa que si eliminas un proyecto, se eliminan automáticamente todas sus tareas. `orphanRemoval = true` elimina tareas que se desvinculen del proyecto sin necesidad de hacer delete explícito.

---

### `Tarea.java` — Tabla `tareas`

```
id | nombre | descripcion | estado | proyecto_id (FK)
```

Tiene la relación inversa, **muchos a uno** con `Proyecto`:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "proyecto_id", nullable = false)
private Proyecto proyecto;
```

> **Concepto clave — FetchType.LAZY:** La tarea no carga los datos del proyecto hasta que los necesites explícitamente. Esto evita consultas innecesarias a la base de datos (el problema N+1).

---

## 5. Capa de Repositorios

Los repositorios son **interfaces** que extienden `JpaRepository`. Spring Data JPA genera la implementación completa en tiempo de ejecución, sin que tengas que escribir SQL.

```java
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    Proyecto findByNombre(String nombre);
}
```

Al extender `JpaRepository<Proyecto, Long>` obtienes gratis:
- `save(entity)` — Crear o actualizar
- `findById(id)` — Buscar por ID
- `findAll()` — Listar todos
- `deleteById(id)` — Eliminar por ID
- `findAll(Pageable)` — Listar con paginación

Y puedes agregar métodos personalizados con solo nombrarlos correctamente (`findByNombre`, `existsByEmail`), Spring los interpreta y genera el SQL:

| Método | SQL generado |
|--------|-------------|
| `findByNombre(String nombre)` | `SELECT * FROM proyectos WHERE nombre = ?` |
| `findByEmail(String email)` | `SELECT * FROM usuarios WHERE email = ?` |
| `existsByEmail(String email)` | `SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?` |

---

## 6. Capa de Servicios

Los servicios contienen la **lógica de negocio**. El patrón utilizado es **interfaz + implementación**:

- `ProyectoService` (interfaz) — define el contrato
- `ProyectoServiceImpl` (clase) — implementa la lógica

### ¿Por qué usar interfaz + implementación?

Permite cambiar la implementación sin modificar los controladores. El controller solo conoce la interfaz, no la implementación concreta.

### Ejemplo — `ProyectoServiceImpl`

```java
@Service
public class ProyectoServiceImpl implements ProyectoService {

    @Transactional
    public void crearProyecto(CreateProyectoDTO dto) {
        // 1. Validación
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        // 2. Mapeo DTO → Entidad
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        // 3. Persistencia
        proyectoRepository.save(proyecto);
    }

    @Transactional(readOnly = true)
    public Page<ListProyectoDTO> listarProyectos(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return proyectoRepository.findAll(pageable)
                .map(p -> new ListProyectoDTO(p.getId(), p.getNombre(), p.getDescripcion()));
    }
}
```

> **Concepto clave — `@Transactional`:** Garantiza que toda la operación es atómica (todo o nada). Si algo falla en medio de la operación, la base de datos vuelve al estado anterior. `readOnly = true` es una optimización para consultas: le indica a Hibernate que no necesita rastrear cambios.

### `UserDetailServiceImpl` — Puente con Spring Security

```java
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
```

Spring Security llama a este método cuando necesita cargar un usuario. En este proyecto lo usa el `JwtFilter` para verificar que el usuario del token existe en la base de datos.

---

## 7. Capa de Controladores (Endpoints)

Los controladores reciben las peticiones HTTP y delegan al servicio correspondiente.

### `AuthController` — Rutas públicas

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/auth/register` | Registrar nuevo usuario |
| POST | `/api/v1/auth/login` | Iniciar sesión, obtener token |

### `ProyectoController` — Rutas protegidas

| Método | Ruta | Rol requerido |
|--------|------|---------------|
| GET | `/api/v1/proyectos` | ADMIN o USER |
| POST | `/api/v1/proyectos` | ADMIN |

### `TareaController` — Rutas protegidas

| Método | Ruta | Rol requerido |
|--------|------|---------------|
| GET | `/api/v1/tareas` | ADMIN o USER |
| POST | `/api/v1/tareas` | ADMIN |

> **Concepto clave — Separación de responsabilidades:** El controller no contiene validaciones de negocio ni acceso directo a la base de datos. Solo recibe la petición, llama al servicio y devuelve la respuesta HTTP.

---

## 8. DTOs — Data Transfer Objects

Los DTOs son clases simples que definen **qué datos entran y salen** de la API. Sirven para desacoplar la estructura interna de la base de datos de lo que se expone al cliente.

### ¿Por qué no usar la entidad directamente?

- Evita exponer campos sensibles (como la contraseña)
- Permite recibir datos en un formato diferente al que se guarda
- Protege contra ataques de mass assignment

### DTOs del proyecto

| DTO | Dirección | Uso |
|-----|-----------|-----|
| `RegisterDTO` | Entrada | Registrar usuario (nombre, email, password, rol?) |
| `LoginDTO` | Entrada | Iniciar sesión (email, password) |
| `CreateProyectoDTO` | Entrada | Crear proyecto (nombre, descripcion) |
| `ListProyectoDTO` | Salida | Listar proyectos (id, nombre, descripcion) |
| `CreateTareaDTO` | Entrada | Crear tarea (nombre, descripcion, estado, proyecto) |
| `ListTareaDTO` | Salida | Listar tareas (id, nombre, descripcion, estado, proyecto) |

---

## 9. Seguridad con JWT

Esta es la parte más compleja del proyecto. Involucra tres componentes trabajando juntos:

```
JwtUtil  ←→  JwtFilter  ←→  SecurityConfig
```

### `JwtUtil` — Generación y validación de tokens

```java
@Component
public class JwtUtil {

    private final String SECRET = "123151747241874281289512321412321";

    // Genera un token con el email como "subject", válido por 10 horas
    public String generateToken(String email) {
        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .compact();
    }

    // Extrae el email del token (también lo valida internamente)
    public String extractEmail(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
```

> **Concepto clave — JWT (JSON Web Token):** Es un string con tres partes separadas por puntos: `header.payload.signature`. El servidor lo firma con una clave secreta. Al recibir el token de vuelta, verifica la firma — si coincide, confía en el contenido. No necesita guardar sesiones en base de datos.

---

### `JwtFilter` — Intercepta cada petición

Se ejecuta **antes** que Spring Security evalúe los permisos. Su trabajo es leer el token del header y configurar el contexto de seguridad:

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Elimina "Bearer "
            try {
                String email = jwtUtil.extractEmail(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailService.loadUserByUsername(email);

                    // Crea el objeto de autenticación con el usuario y sus roles
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Registra la autenticación en el contexto de Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Token inválido o expirado — Spring Security denegará el acceso
            }
        }

        filterChain.doFilter(request, response); // Continuar con el resto de filtros
    }
}
```

> **Concepto clave — `SecurityContextHolder`:** Es el lugar donde Spring Security almacena quién está autenticado durante el procesamiento de una petición. Si está vacío, la petición se trata como anónima y se deniega el acceso a rutas protegidas.

---

### `SecurityConfig` — Reglas de acceso

Define **quién puede acceder a qué** y registra el filtro JWT en la cadena de seguridad:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())           // No necesario en APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()                         // Público
                .requestMatchers(HttpMethod.POST, "/api/v1/proyectos").hasRole("ADMIN") // Solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/tareas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/proyectos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/tareas/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin sesiones HTTP
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Registrar filtro JWT

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptación segura de contraseñas
    }
}
```

> **Concepto clave — `STATELESS`:** En lugar de guardar sesiones en el servidor, cada petición debe incluir su propio token JWT. Esto hace la API escalable (cualquier servidor puede manejar cualquier petición).

> **Concepto clave — `addFilterBefore`:** Inserta el `JwtFilter` **antes** del filtro de autenticación por usuario/contraseña de Spring. Así, cuando Spring evalúa los permisos, el contexto ya tiene al usuario autenticado.

---

## 10. Flujo completo de autenticación

```
┌─────────────────────────────────────────────────────────────────┐
│  1. REGISTRO                                                    │
│                                                                 │
│  Cliente  ──POST /api/v1/auth/register──►  AuthController      │
│           { email, password, nombre, rol }    │                 │
│                                               ▼                 │
│                                        PasswordEncoder          │
│                                        .encode(password)        │
│                                               │                 │
│                                               ▼                 │
│                                        UsuarioRepository        │
│                                        .save(usuario)           │
│  Cliente  ◄── 200 "Usuario creado" ──────────┘                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  2. LOGIN                                                       │
│                                                                 │
│  Cliente  ──POST /api/v1/auth/login──►  AuthController         │
│           { email, password }               │                   │
│                                             ▼                   │
│                                    UsuarioRepository            │
│                                    .findByEmail(email)          │
│                                             │                   │
│                                             ▼                   │
│                                    PasswordEncoder              │
│                                    .matches(raw, encoded) ──►  │
│                                             │                   │
│                                             ▼                   │
│                                    JwtUtil                      │
│                                    .generateToken(email)        │
│  Cliente  ◄── 200 "eyJhbGci..." ────────────┘                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  3. PETICIÓN AUTENTICADA                                        │
│                                                                 │
│  Cliente  ──GET /api/v1/proyectos──►  JwtFilter                │
│  Header: Authorization: Bearer <token>    │                     │
│                                           ▼                     │
│                                  JwtUtil.extractEmail(token)    │
│                                           │                     │
│                                           ▼                     │
│                              UserDetailServiceImpl              │
│                              .loadUserByUsername(email)         │
│                                           │                     │
│                                           ▼                     │
│                              SecurityContextHolder              │
│                              .setAuthentication(...)            │
│                                           │                     │
│                                           ▼                     │
│                              SecurityConfig evalúa rol          │
│                              ¿tiene ROLE_USER o ROLE_ADMIN?     │
│                                           │                     │
│                                           ▼                     │
│                              ProyectoController                 │
│                              .listarProyectos()                 │
│                                           │                     │
│                                           ▼                     │
│                              ProyectoService                    │
│                              .listarProyectos(page, size)       │
│                                           │                     │
│                                           ▼                     │
│                              ProyectoRepository                 │
│                              .findAll(pageable)                 │
│  Cliente  ◄── 200 { proyectos... } ────────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 11. Cómo levantar el proyecto

### Prerequisitos
- Java 17+
- Maven
- Docker y Docker Compose

### Pasos

```bash
# 1. Levantar la base de datos MySQL
docker-compose up -d

# 2. Compilar y correr la aplicación
./mvnw spring-boot:run
```

Hibernate creará automáticamente las tablas al iniciar gracias a `spring.jpa.hibernate.ddl-auto=update`.

La API estará disponible en: `http://localhost:8080`

---

## 12. Endpoints de la API

### Autenticación

```bash
# Registrar usuario
POST /api/v1/auth/register
Content-Type: application/json

{
  "nombre": "Juan Admin",
  "email": "admin@test.com",
  "password": "123456",
  "rol": "ADMIN"         # Opcional, por defecto USER
}

# Iniciar sesión
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@test.com",
  "password": "123456"
}
# Respuesta: "eyJhbGciOiJIUzI1NiJ9..."
```

### Proyectos (requieren token)

```bash
# Listar proyectos (ADMIN o USER)
GET /api/v1/proyectos?page=0&size=5
Authorization: Bearer <tu_token>

# Crear proyecto (solo ADMIN)
POST /api/v1/proyectos
Authorization: Bearer <tu_token>
Content-Type: application/json

{
  "nombre": "Proyecto Alpha",
  "descripcion": "Descripción del proyecto"
}
```

### Tareas (requieren token)

```bash
# Listar tareas (ADMIN o USER)
GET /api/v1/tareas?page=0&size=5
Authorization: Bearer <tu_token>

# Crear tarea (solo ADMIN)
POST /api/v1/tareas
Authorization: Bearer <tu_token>
Content-Type: application/json

{
  "nombre": "Tarea 1",
  "descripcion": "Descripción de la tarea",
  "estado": "PENDIENTE",
  "proyecto": "Proyecto Alpha"
}
```

> Los estados válidos para una tarea son: `PENDIENTE`, `EN_PROGRESO`, `COMPLETADA`

---

## 13. Errores comunes y su causa

| Código | Causa | Solución |
|--------|-------|----------|
| `403 Forbidden` | Token ausente, inválido o expirado | Incluir el header `Authorization: Bearer <token>` con un token válido |
| `403 Forbidden` | Token válido pero rol insuficiente | El usuario tiene rol `USER` e intenta crear (POST). Usar un usuario `ADMIN` |
| `400 Bad Request` | Email ya registrado | Usar un email diferente |
| `400 Bad Request` | Estado de tarea inválido | Usar `PENDIENTE`, `EN_PROGRESO` o `COMPLETADA` |
| `400 Bad Request` | Nombre del proyecto no existe | Crear el proyecto antes de asignarle tareas |
| `401 Unauthorized` | Contraseña incorrecta en login | Verificar credenciales |
