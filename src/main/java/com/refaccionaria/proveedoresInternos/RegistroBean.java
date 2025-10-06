package com.refaccionaria.proveedoresInternos;

import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;



import jakarta.faces.context.FacesContext;
import java.io.Serializable;

@Named("registroBean")
@SessionScoped
public class RegistroBean implements Serializable {

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String usuario;
    private String password;
    private String rol;

    // ==============================
    //   MÉTODO PARA REGISTRAR
    // ==============================
public String registrar() {
    try {
        if (usuario == null || usuario.isEmpty() || password == null || password.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "El usuario y la contraseña son obligatorios."));
            return null;
        }

        // --- Simulación de registro ---
        System.out.println("=== Usuario registrado ===");
        System.out.println("Nombre: " + nombre);
        System.out.println("Usuario: " + usuario);
        System.out.println("Correo: " + correo);
        System.out.println("Rol: " + rol);
        System.out.println("===========================");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Registro exitoso", "Usuario creado correctamente."));

        // 🔥 Redirección sin advertencia
        return "/login.xhtml?faces-redirect=true";

    } catch (Exception e) {
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "Ocurrió un problema al registrar el usuario."));
        return null;
    }
}

    // ==============================
    //   GETTERS Y SETTERS
    // ==============================
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
