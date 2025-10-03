package com.refaccionaria.proveedoresInternos.models;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author RMD
 */
public class Producto {
    
    private String id;
    private String codigoProducto;
    private String nombre;
    private String marca;
    private String categoria;
    private double precio;
    private Map<String, String> caracteristicas;
    private Date fechaCreacion;
    private Date ultimaActualizacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Map<String, String> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Map<String, String> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    
    
}
