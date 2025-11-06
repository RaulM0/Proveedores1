package com.refaccionaria.proveedoresInternos.models;

import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.util.Date;

public class Usuario {

    @BsonId
    private ObjectId id;

    @BsonProperty("usuario")
    private String usuario;

    @BsonProperty("nombre")
    private String nombre;

    @BsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @BsonProperty("apellido_materno")
    private String apellidoMaterno;

    @BsonProperty("correo")
    private String correo;

    @BsonProperty("password")
    private String password; // 👈 cambiado a "password" (coincide con Mongo)

    @BsonProperty("rol")
    private String rol;

    @BsonProperty("fecha_creacion")
    private Date fechaCreacion;

    @BsonProperty("activo")
    private boolean activo;

    public Usuario() {}

    public Usuario(String usuario, String nombre, String apellidoPaterno, String apellidoMaterno,
                   String correo, String rol) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.rol = rol;
        this.fechaCreacion = new Date();
        this.activo = true;
    }

    // Getters y Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
