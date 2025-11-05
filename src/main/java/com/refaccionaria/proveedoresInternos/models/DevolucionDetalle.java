package com.refaccionaria.proveedoresInternos.models; // (O el paquete donde pongas tus modelos)

import java.io.Serializable;

/**
 * Representa el sub-documento (anidado) con la información
 * de cada producto que se está devolviendo.
 */
public class DevolucionDetalle implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String producto_id;       // El ObjectId del producto (de la col. 'productos')
    private String nombre;            // El nombre del producto (para fácil lectura)
    private int cantidadDevuelta;     // La cantidad que se devolvió
    private double precioUnitario;    // El precio al que se vendió originalmente
    private double subtotalDevuelto;  // (cantidadDevuelta * precioUnitario)

    
    // --- Getters y Setters ---

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

    public int getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(int cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotalDevuelto() {
        return subtotalDevuelto;
    }

    public void setSubtotalDevuelto(double subtotalDevuelto) {
        this.subtotalDevuelto = subtotalDevuelto;
    }
}