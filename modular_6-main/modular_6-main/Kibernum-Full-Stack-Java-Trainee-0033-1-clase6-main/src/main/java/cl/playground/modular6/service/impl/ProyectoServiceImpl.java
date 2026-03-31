package cl.playground.modular6.service.impl;

import cl.playground.modular6.dto.CreateProyectoDTO;
import cl.playground.modular6.dto.ListProyectoDTO;
import cl.playground.modular6.model.Proyecto;
import cl.playground.modular6.repository.ProyectoRepository;
import cl.playground.modular6.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Autowired
    public ProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    @Transactional
    public void crearProyecto(CreateProyectoDTO createProyectoDTO) {
        validarProyecto(createProyectoDTO);

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(createProyectoDTO.getNombre());
        proyecto.setDescripcion(createProyectoDTO.getDescripcion());

        proyectoRepository.save(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListProyectoDTO> listarProyectos(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 0 y el tamaño debe ser mayor a 0");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Proyecto> proyectosPage = proyectoRepository.findAll(pageable);

        List<ListProyectoDTO> dtos = proyectosPage.getContent()
            .stream()
            .map(proyecto -> {
                ListProyectoDTO dto = new ListProyectoDTO();
                dto.setId(proyecto.getId());
                dto.setNombre(proyecto.getNombre());
                dto.setDescripcion(proyecto.getDescripcion());
                return dto;
            })
            .toList();

        return new PageImpl<>(dtos, pageable, proyectosPage.getTotalElements());
    }

    private void validarProyecto(CreateProyectoDTO createProyectoDTO) {
        // Validar que el nombre del proyecto no esté vacío
        if (createProyectoDTO.getNombre() == null || createProyectoDTO.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        // Validar que la descripción del proyecto no esté vacía
        if (createProyectoDTO.getDescripcion() == null || createProyectoDTO.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción del proyecto es obligatoria");
        }
    }
}
