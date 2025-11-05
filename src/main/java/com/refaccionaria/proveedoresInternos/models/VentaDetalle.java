package com.refaccionaria.proveedoresInternos.models;

import java.io.Serializable;

// Esta clase representa el sub-documento 'detalle'
public class VentaDetalle implements Serializable {

    private static final long serialVersionUID = 1L;

    private String producto_id;
    private String nombre;
    private int cantidad;
    private double precio_unitario;
    private double subtotal;

    // --- CONSTRUCTOR VACÍO ---
    // (Requerido para el mapeo en VentaService)
    public VentaDetalle() {
    }

    // Getters y Setters
    public String getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(String producto_id) {
        this.producto_id = producto_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
