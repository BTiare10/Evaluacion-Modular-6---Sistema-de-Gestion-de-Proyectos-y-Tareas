package cl.playground.modular6.dto;

public class CreateTareaDTO {

    private String nombre;
    private String descripcion;
    private String estado;
    private String proyecto;

    public CreateTareaDTO() {
    }

    public CreateTareaDTO(String nombre, String descripcion, String estado, String proyecto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.proyecto = proyecto;
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
        return "CreateTareaDTO{" +
            "nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", estado='" + estado + '\'' +
            ", proyecto='" + proyecto + '\'' +
            '}';
    }
}
