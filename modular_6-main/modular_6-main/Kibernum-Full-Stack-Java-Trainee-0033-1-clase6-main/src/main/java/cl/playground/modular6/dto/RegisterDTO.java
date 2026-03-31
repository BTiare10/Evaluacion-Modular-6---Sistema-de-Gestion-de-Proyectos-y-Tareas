package cl.playground.modular6.dto;

import cl.playground.modular6.model.Rol;

public class RegisterDTO {

    private String nombre;
    private String email;
    private String password;

    // opcional (puedes ignorarlo y setear USER por defecto)
    private Rol rol;

    // getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}