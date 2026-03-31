package cl.playground.modular6.service.impl;

import cl.playground.modular6.dto.CreateTareaDTO;
import cl.playground.modular6.dto.ListTareaDTO;
import cl.playground.modular6.model.Proyecto;
import cl.playground.modular6.model.Tarea;
import cl.playground.modular6.repository.ProyectoRepository;
import cl.playground.modular6.repository.TareaRepository;
import cl.playground.modular6.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TareaServiceImpl implements TareaService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    private static final List<String> ESTADOS = List.of("PENDIENTE", "EN_PROGRESO", "COMPLETADA");

    @Autowired
    public TareaServiceImpl(ProyectoRepository proyectoRepository, TareaRepository tareaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
    }


    @Override
    @Transactional
    public void crearTarea(CreateTareaDTO dto) {
        validarTarea(dto);

        // Recuperar el proyecto asociado a la tarea
        Proyecto proyecto = proyectoRepository.findByNombre(dto.getProyecto());
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto '" + dto.getProyecto() + "' no existe");
        }

        // Mapeamos el DTO a la entidad Tarea
        Tarea tarea = new Tarea();
        tarea.setNombre(dto.getNombre());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setEstado(dto.getEstado());
        tarea.setProyecto(proyecto);

        // Guardamos la tarea en la base de datos
        tareaRepository.save(tarea);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListTareaDTO> listarTareas(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 0 y el tamaño debe ser mayor a 0");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Tarea> tareasPage = tareaRepository.findAll(pageable);

        List<ListTareaDTO> dtos = tareasPage.getContent()
            .stream()
            .map(tarea -> {
                ListTareaDTO dto = new ListTareaDTO();
                dto.setId(tarea.getId());
                dto.setNombre(tarea.getNombre());
                dto.setDescripcion(tarea.getDescripcion());
                dto.setEstado(tarea.getEstado());
                dto.setProyecto(tarea.getProyecto().getNombre());
                return dto;
            })
            .toList();


        return new PageImpl<>(dtos, pageable, tareasPage.getTotalElements());
    }

    private void validarTarea(CreateTareaDTO createTareaDTO) {
        if (createTareaDTO.getNombre() == null || createTareaDTO.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la tarea es obligatorio");
        }
        if (createTareaDTO.getDescripcion() == null || createTareaDTO.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la tarea es obligatoria");
        }
        if (createTareaDTO.getEstado() == null || createTareaDTO.getEstado().isEmpty()) {
            throw new IllegalArgumentException("El estado de la tarea es obligatorio");
        }
        if (!ESTADOS.contains(createTareaDTO.getEstado())) {
            throw new IllegalArgumentException("El estado de la tarea debe ser uno de: " + ESTADOS);
        }

    }
}
