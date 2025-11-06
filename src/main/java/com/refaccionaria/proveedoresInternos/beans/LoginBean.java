package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Usuario;
import com.refaccionaria.proveedoresInternos.services.UsuarioService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

// 🔹 Importaciones necesarias para MongoDB
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String usuario;
    private String password;
    private Usuario usuarioActual; // ✅ Objeto completo del usuario logueado

    private final UsuarioService servicio = new UsuarioService();

    // 🔹 Configuración MongoDB
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static MongoClient mongoClient;

    // 🔹 Método de autenticación
    public String login() {
        Usuario encontrado = servicio.buscarPorUsuario(usuario);

        if (encontrado != null && encontrado.getPassword() != null
                && encontrado.getPassword().equals(password)) {

            // ✅ Guardar el usuario completo en la sesión
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("usuarioActual", encontrado);

            this.usuarioActual = encontrado;

            // Mostrar mensaje y redirigir
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Bienvenido", encontrado.getNombre()));

            return "/dashboard.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "Usuario o contraseña incorrectos"));
        return null;
    }

    // 🔹 Cierre de sesión
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    // 🔹 Obtener nombre visible para el navbar
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombre() : "Invitado";
    }

    // 🔹 Obtener rol (para ocultar secciones si se requiere)
    public String getRolUsuario() {
        return usuarioActual != null ? usuarioActual.getRol() : "";
    }

    // ✅ 🔹 MÉTODO COMPATIBLE con los otros beans
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DB_NAME);
    }

    // 🔹 Getters & Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario usuarioActual) { this.usuarioActual = usuarioActual; }
}
