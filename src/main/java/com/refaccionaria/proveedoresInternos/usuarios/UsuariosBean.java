package com.refaccionaria.proveedoresInternos.usuarios;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

@Named("usuariosBean")
@SessionScoped
public class UsuariosBean implements Serializable {

    private List<Usuario> listaUsuarios;
    private String usuarioSeleccionado;

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String usuario;
    private String rolSeleccionado;

    public UsuariosBean() {
        // Usuarios simulados
        listaUsuarios = Arrays.asList(
            new Usuario("u1", "Jonathan", "Díaz", "Ramírez", "jonathan@empresa.com", "ADMIN"),
            new Usuario("u2", "María", "López", "García", "maria@empresa.com", "VENTAS"),
            new Usuario("u3", "Carlos", "Gómez", "Torres", "carlos@empresa.com", "ALMACEN")
        );
    }

    // Cargar datos simulados
    public void cargarDatosUsuario() {
        for (Usuario u : listaUsuarios) {
            if (u.getUsuario().equals(usuarioSeleccionado)) {
                this.nombre = u.getNombre();
                this.apellidoPaterno = u.getApellidoPaterno();
                this.apellidoMaterno = u.getApellidoMaterno();
                this.correo = u.getCorreo();
                this.usuario = u.getUsuario();
                this.rolSeleccionado = u.getRol();
                break;
            }
        }
    }

    // Guardar cambios simulados
public String guardarCambios() {
    System.out.println("=== Cambios guardados ===");
    System.out.println("Usuario: " + usuario);
    System.out.println("Nuevo rol: " + rolSeleccionado);

    FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_INFO,
        "Cambios guardados", "El rol del usuario fue actualizado correctamente."));

    // 🔹 Redirige al dashboard.xhtml
    return "/dashboard.xhtml?faces-redirect=true";
}


    // Getters y Setters
    public List<Usuario> getListaUsuarios() { return listaUsuarios; }

    public String getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(String usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }

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

    public String getRolSeleccionado() { return rolSeleccionado; }
    public void setRolSeleccionado(String rolSeleccionado) { this.rolSeleccionado = rolSeleccionado; }
}
