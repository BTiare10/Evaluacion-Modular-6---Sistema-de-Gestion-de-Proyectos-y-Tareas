package cl.playground.modular6.service;

import cl.playground.modular6.dto.CreateProyectoDTO;
import cl.playground.modular6.dto.ListProyectoDTO;
import org.springframework.data.domain.Page;

public interface ProyectoService {

    void crearProyecto(CreateProyectoDTO createProyectoDTO);
    Page<ListProyectoDTO> listarProyectos(int page, int size);
}
