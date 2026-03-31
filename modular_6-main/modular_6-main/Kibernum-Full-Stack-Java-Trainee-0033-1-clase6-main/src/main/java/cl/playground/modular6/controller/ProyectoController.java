package cl.playground.modular6.controller;

import cl.playground.modular6.dto.CreateProyectoDTO;
import cl.playground.modular6.dto.ListProyectoDTO;
import cl.playground.modular6.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proyectos") // posterior al puerto va...
public class ProyectoController {

    private final ProyectoService proyectoService;

    @Autowired
    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public ResponseEntity<Page<ListProyectoDTO>> listarProyectos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<ListProyectoDTO> proyectos = proyectoService.listarProyectos(page, size);
        return ResponseEntity.ok(proyectos);
    }

    @PostMapping
    public ResponseEntity<Void> crearProyecto(@RequestBody CreateProyectoDTO dto) {
        proyectoService.crearProyecto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
