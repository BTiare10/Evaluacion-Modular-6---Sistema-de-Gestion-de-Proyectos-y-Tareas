# Spring Security — Diseño e Implementación

> Proyecto: `modular6` | Spring Boot `3.5.13` | Java `17`

---

## 1. Contexto actual

El proyecto expone 4 endpoints REST sin ningún tipo de protección:

| Método | Ruta                  | Descripción              |
|--------|-----------------------|--------------------------|
| GET    | /api/v1/proyectos     | Listar proyectos (paginado) |
| POST   | /api/v1/proyectos     | Crear proyecto           |
| GET    | /api/v1/tareas        | Listar tareas (paginado)  |
| POST   | /api/v1/tareas        | Crear tarea              |

La dependencia de Spring Security está comentada en `pom.xml`. No existe ninguna entidad de usuario ni configuración de seguridad.

---

## 2. Dependencias a agregar en `pom.xml`

Descomentar las existentes y agregar la librería JWT:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- Spring Security Test (descomentar) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. Estructura de archivos nueva

```
src/main/java/cl/playground/modular6/
├── model/
│   ├── Proyecto.java           (existente)
│   ├── Tarea.java              (existente)
│   ├── Usuario.java            ← NUEVO
│   └── Rol.java                ← NUEVO (enum)
├── repository/
│   ├── ProyectoRepository.java (existente)
│   ├── TareaRepository.java    (existente)
│   └── UsuarioRepository.java  ← NUEVO
├── dto/
│   ├── ...                     (existentes)
│   ├── RegisterDTO.java        ← NUEVO
│   ├── LoginDTO.java           ← NUEVO
│   └── AuthResponseDTO.java    ← NUEVO
├── service/
│   ├── ...                     (existentes)
│   └── impl/
│       └── UserDetailsServiceImpl.java  ← NUEVO
├── security/
│   ├── JwtUtil.java            ← NUEVO
│   ├── JwtFilter.java          ← NUEVO
│   └── SecurityConfig.java     ← NUEVO
└── controller/
    ├── ProyectoController.java  (existente)
    ├── TareaController.java     (existente)
    └── AuthController.java      ← NUEVO
```

---

## 4. Modelo: `Rol.java` (enum)

```java
package cl.playground.modular6.model;

public enum Rol {
    ADMIN,
    USER
}
```

**Descripción de roles:**

| Rol   | Permisos                                              |
|-------|-------------------------------------------------------|
| ADMIN | Acceso total — puede crear y listar proyectos y tareas |
| USER  | Solo lectura — puede listar proyectos y tareas         |

---

## 5. Entidad: `Usuario.java`

```java
package cl.playground.modular6.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    // ── Constructores ──────────────────────────────────────────
    public Usuario() {}

    public Usuario(String email, String password, String nombre, Rol rol) {
        this.email    = email;
        this.password = password;
        this.nombre   = nombre;
        this.rol      = rol;
    }

    // ── UserDetails ────────────────────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return email;                // Spring Security usa email como username
    }

    @Override
    public boolean isAccountNonExpired()     { return true; }
    @Override
    public boolean isAccountNonLocked()      { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled()               { return true; }

    // ── Getters / Setters ──────────────────────────────────────
    public Long getId()             { return id; }
    public String getEmail()        { return email; }
    public Rol getRol()             { return rol; }
    public String getNombre()       { return nombre; }
    public void setEmail(String v)  { this.email = v; }
    public void setPassword(String v){ this.password = v; }
    public void setNombre(String v) { this.nombre = v; }
    public void setRol(Rol v)       { this.rol = v; }
}
```

**Tabla generada en MySQL:**

```sql
CREATE TABLE usuarios (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre   VARCHAR(50)  NOT NULL,
    rol      ENUM('ADMIN','USER') NOT NULL
);
```

---

## 6. Repositorio: `UsuarioRepository.java`

```java
package cl.playground.modular6.repository;

import cl.playground.modular6.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

---

## 7. DTOs de autenticación

### `RegisterDTO.java`
```java
public class RegisterDTO {
    private String nombre;
    private String email;
    private String password;
    private Rol rol;           // ADMIN o USER
    // getters y setters...
}
```

### `LoginDTO.java`
```java
public class LoginDTO {
    private String email;
    private String password;
    // getters y setters...
}
```

### `AuthResponseDTO.java`
```java
public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private String nombre;
    private String rol;
    // getters y setters...
}
```

---

## 8. Utilidad JWT: `JwtUtil.java`

```java
package cl.playground.modular6.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;   // en milisegundos

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public String extraerEmail(String token) {
        return parsear(token).getPayload().getSubject();
    }

    public boolean esValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token);
    }
}
```

---

## 9. Filtro JWT: `JwtFilter.java`

```java
package cl.playground.modular6.security;

import cl.playground.modular6.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.esValido(token)) {
                String email = jwtUtil.extraerEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
```

---

## 10. UserDetailsService: `UserDetailsServiceImpl.java`

```java
package cl.playground.modular6.service.impl;

import cl.playground.modular6.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
```

---

## 11. Configuración de seguridad: `SecurityConfig.java`

```java
package cl.playground.modular6.security;

import cl.playground.modular6.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired private JwtFilter jwtFilter;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos — registro y login
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Solo ADMIN puede crear
                .requestMatchers(HttpMethod.POST, "/api/v1/proyectos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/tareas").hasRole("ADMIN")

                // USER y ADMIN pueden listar
                .requestMatchers(HttpMethod.GET, "/api/v1/proyectos").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/tareas").hasAnyRole("ADMIN", "USER")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## 12. Controlador de autenticación: `AuthController.java`

```java
package cl.playground.modular6.controller;

import cl.playground.modular6.dto.AuthResponseDTO;
import cl.playground.modular6.dto.LoginDTO;
import cl.playground.modular6.dto.RegisterDTO;
import cl.playground.modular6.model.Usuario;
import cl.playground.modular6.repository.UsuarioRepository;
import cl.playground.modular6.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        Usuario usuario = new Usuario(
            dto.getEmail(),
            passwordEncoder.encode(dto.getPassword()),
            dto.getNombre(),
            dto.getRol()
        );
        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO dto) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow();
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setNombre(usuario.getNombre());
        response.setRol(usuario.getRol().name());

        return ResponseEntity.ok(response);
    }
}
```

---

## 13. Propiedades adicionales en `application.properties`

```properties
# JWT
jwt.secret=clave-super-secreta-de-al-menos-256-bits-para-hmac-sha256
jwt.expiration=86400000
# 86400000 ms = 24 horas
```

> **Importante:** En producción, la `jwt.secret` debe ser una cadena aleatoria de mínimo 32 caracteres y almacenarse como variable de entorno, no en el archivo de propiedades.

---

## 14. Resumen de endpoints y permisos

| Método | Ruta                    | Rol requerido    | Descripción             |
|--------|-------------------------|------------------|-------------------------|
| POST   | /api/v1/auth/register   | Público          | Registrar usuario       |
| POST   | /api/v1/auth/login      | Público          | Obtener token JWT       |
| GET    | /api/v1/proyectos       | ADMIN / USER     | Listar proyectos        |
| POST   | /api/v1/proyectos       | ADMIN            | Crear proyecto          |
| GET    | /api/v1/tareas          | ADMIN / USER     | Listar tareas           |
| POST   | /api/v1/tareas          | ADMIN            | Crear tarea             |

---

## 15. Flujo de autenticación

```
Cliente                          Servidor
  │                                 │
  │── POST /api/v1/auth/login ──────▶│
  │   { email, password }            │  1. Valida credenciales
  │                                 │  2. Genera JWT firmado
  │◀── 200 OK { token, rol } ───────│
  │                                 │
  │── GET /api/v1/proyectos ────────▶│
  │   Authorization: Bearer <token>  │  3. JwtFilter extrae token
  │                                 │  4. Valida firma y expiración
  │                                 │  5. Carga usuario en contexto
  │◀── 200 OK [ proyectos... ] ─────│  6. SecurityConfig verifica rol
```

---

## 16. Diagrama de relaciones entre entidades (con seguridad)

```
┌─────────────┐       ┌─────────────────┐
│   Usuario   │       │      Rol        │
│─────────────│       │─────────────────│
│ id          │       │ ADMIN           │
│ email       │──────▶│ USER            │
│ password    │       └─────────────────┘
│ nombre      │
│ rol (enum)  │
└─────────────┘

┌─────────────┐       ┌─────────────────┐
│   Proyecto  │       │     Tarea       │
│─────────────│       │─────────────────│
│ id          │◀──────│ id              │
│ nombre      │ 1:N   │ nombre          │
│ descripcion │       │ descripcion     │
└─────────────┘       │ estado          │
                      │ proyecto_id (FK)│
                      └─────────────────┘
```

> `Usuario` no tiene relación directa con `Proyecto` o `Tarea` en el modelo actual. La seguridad se aplica a nivel de **rol** sobre los endpoints REST.