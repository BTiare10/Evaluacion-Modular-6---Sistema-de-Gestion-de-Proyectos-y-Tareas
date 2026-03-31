package cl.playground.modular6.service;

import cl.playground.modular6.dto.CreateTareaDTO;
import cl.playground.modular6.dto.ListTareaDTO;
import org.springframework.data.domain.Page;

public interface TareaService {

    void crearTarea(CreateTareaDTO dto);
    Page<ListTareaDTO> listarTareas(int page, int size);
}
