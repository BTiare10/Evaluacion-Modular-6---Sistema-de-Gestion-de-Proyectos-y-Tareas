package cl.playground.modular6.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 255)
    private String descripcion;
    @Column(nullable = false, length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    public Tarea() {
    }

    public Tarea(Long id, String nombre, String descripcion, String estado, Proyecto proyecto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.proyecto = proyecto;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    @Override
    public String toString() {
        return "Tarea{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", estado='" + estado + '\'' +
            ", proyecto=" + proyecto +
            '}';
    }
}
