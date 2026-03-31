package cl.playground.modular6.controller;

import cl.playground.modular6.dto.LoginDTO;
import cl.playground.modular6.dto.RegisterDTO;
import cl.playground.modular6.model.Rol;
import cl.playground.modular6.model.Usuario;
import cl.playground.modular6.repository.UsuarioRepository;
import cl.playground.modular6.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email ya existe");
        }

        Usuario user = new Usuario();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNombre(dto.getNombre());
        user.setRol(dto.getRol() != null ? dto.getRol() : Rol.USER);

        usuarioRepository.save(user);

        return ResponseEntity.ok("Usuario creado");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {

        Usuario user = usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(token);
    }
}
