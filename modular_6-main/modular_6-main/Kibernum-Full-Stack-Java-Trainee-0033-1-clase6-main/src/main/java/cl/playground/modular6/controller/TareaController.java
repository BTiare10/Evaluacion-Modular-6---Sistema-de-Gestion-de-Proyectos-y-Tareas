package cl.playground.modular6.controller;

import cl.playground.modular6.dto.CreateProyectoDTO;
import cl.playground.modular6.dto.CreateTareaDTO;
import cl.playground.modular6.dto.ListProyectoDTO;
import cl.playground.modular6.dto.ListTareaDTO;
import cl.playground.modular6.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tareas")
public class TareaController {

    private final TareaService tareaService;

    @Autowired
    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<Page<ListTareaDTO>> listarTareas(
        @RequestParam(defaultValue = "0")
        int page,
        @RequestParam(defaultValue = "5")
        int size) {
        Page<ListTareaDTO> tareas = tareaService.listarTareas(page, size);
        return ResponseEntity.ok(tareas);
    }

    @PostMapping
    public ResponseEntity<?> crearTareas(@RequestBody CreateTareaDTO dto) {
        try {
            tareaService.crearTarea(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
