package com.refaccionaria.proveedoresInternos.usuarios;

import com.refaccionaria.proveedoresInternos.models.Usuario;
import com.refaccionaria.proveedoresInternos.services.UsuarioService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named("usuariosBean")
@SessionScoped
public class UsuariosBean implements Serializable {

    private final UsuarioService servicio = new UsuarioService();
    private List<Usuario> listaUsuarios;

    private String usuarioSeleccionado;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String usuario;
    private String rolSeleccionado;

    public UsuariosBean() {
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        listaUsuarios = servicio.listarTodos();

        // 🔒 Obtener el usuario actual logueado desde la sesión
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
        Usuario usuarioActual = (Usuario) sessionMap.get("usuarioActual");

        // 🚫 Remover al usuario actual de la lista para que no aparezca en el combo
        if (usuarioActual != null && listaUsuarios != null) {
            listaUsuarios.removeIf(u ->
                u.getUsuario() != null &&
                u.getUsuario().equalsIgnoreCase(usuarioActual.getUsuario())
            );
        }
    }

    public void cargarDatosUsuario() {
        if (usuarioSeleccionado == null || usuarioSeleccionado.isEmpty()) return;

        Usuario u = servicio.buscarPorUsuario(usuarioSeleccionado);
        if (u != null) {
            this.nombre = u.getNombre();
            this.apellidoPaterno = u.getApellidoPaterno();
            this.apellidoMaterno = u.getApellidoMaterno();
            this.correo = u.getCorreo();
            this.usuario = u.getUsuario();
            this.rolSeleccionado = u.getRol();
        }
    }

    public String guardarCambios() {
        Usuario u = servicio.buscarPorUsuario(usuario);
        if (u != null) {
            u.setNombre(nombre);
            u.setApellidoPaterno(apellidoPaterno);
            u.setApellidoMaterno(apellidoMaterno);
            u.setCorreo(correo);
            u.setRol(rolSeleccionado);
            servicio.guardar(u);
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Cambios guardados", "El usuario fue actualizado correctamente."));

        return "/dashboard.xhtml?faces-redirect=true";
    }

    // 🔒 Verificación de acceso solo para ADMIN
    public void verificarAccesoAdmin() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
        Usuario usuarioActual = (Usuario) sessionMap.get("usuarioActual");

        if (usuarioActual == null) {
            try {
                ctx.getExternalContext().redirect(ctx.getExternalContext().getRequestContextPath() + "/login.xhtml");
                ctx.responseComplete();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String rol = usuarioActual.getRol() != null ? usuarioActual.getRol().trim().toUpperCase() : "";
        if (!"ADMIN".equals(rol)) {
            try {
                ctx.getExternalContext().redirect(ctx.getExternalContext().getRequestContextPath() + "/dashboard.xhtml");
                ctx.responseComplete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Getters & Setters
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
