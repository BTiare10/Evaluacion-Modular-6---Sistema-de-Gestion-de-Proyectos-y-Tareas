package cl.playground.modular6.dto;

public class ListTareaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String estado;
    private String proyecto;

    public ListTareaDTO() {
    }

    public ListTareaDTO(Long id, String nombre, String descripcion, String estado, String proyecto) {
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

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    @Override
    public String toString() {
        return "ListTareaDTO{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", estado='" + estado + '\'' +
            ", proyecto='" + proyecto + '\'' +
            '}';
    }
}
