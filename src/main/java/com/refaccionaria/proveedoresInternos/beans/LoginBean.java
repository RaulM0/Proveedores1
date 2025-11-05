package com.refaccionaria.proveedoresInternos.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static MongoClient mongoClient;

    private String usuario;
    private String password;
    private String usuarioActual;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DB_NAME);
    }

    // 👉 Método llamado por el botón de tu XHTML
    public String login() {
        if (autenticarUsuario(usuario, password)) {
            addMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", usuarioActual);
            return "dashboard?faces-redirect=true"; // cambia "dashboard" por tu página principal
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos");
            return null;
        }
    }

    private boolean autenticarUsuario(String usuario, String password) {
        try {
            MongoDatabase db = getDatabase();
            MongoCollection<Document> usuarios = db.getCollection("usuarios");

            Document query = new Document("usuario", usuario)
                    .append("password", password);

            Document user = usuarios.find(query).first();

            if (user != null) {
                this.usuarioActual = user.getString("nombre");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo conectar con la base de datos");
        }
        return false;
    }

    private void addMessage(FacesMessage.Severity severity, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, titulo, detalle));
    }

    // 🔹 Getters y Setters obligatorios
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }
}
