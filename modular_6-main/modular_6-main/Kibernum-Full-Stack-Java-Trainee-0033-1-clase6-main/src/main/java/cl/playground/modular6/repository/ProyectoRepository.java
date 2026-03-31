package cl.playground.modular6.repository;

import cl.playground.modular6.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    Proyecto findByNombre(String nombre);

}
