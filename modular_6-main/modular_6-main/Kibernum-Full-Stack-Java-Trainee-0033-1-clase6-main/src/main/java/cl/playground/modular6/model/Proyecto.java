package cl.playground.modular6.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 255)
    private String descripcion;
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas;

    public Proyecto() {
    }

    public Proyecto(Long id, String nombre, String descripcion, List<Tarea> tareas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tareas = tareas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }

    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
    }

    @Override
    public String toString() {
        return "Proyecto{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", tareas=" + tareas +
            '}';
    }
}
