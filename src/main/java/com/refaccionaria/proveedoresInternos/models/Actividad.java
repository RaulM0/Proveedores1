package com.refaccionaria.proveedoresInternos.models;

import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;
import java.util.Date;

public class Actividad {

    @BsonId
    private ObjectId id;
    private Date fecha;
    private String operacion;
    private String usuario;

    public Actividad() {}

    public Actividad(String operacion, String usuario) {
        this.fecha = new Date();
        this.operacion = operacion;
        this.usuario = usuario;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
