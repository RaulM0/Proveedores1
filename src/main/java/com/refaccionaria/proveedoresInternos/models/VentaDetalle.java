package com.refaccionaria.proveedoresInternos.models;

import java.io.Serializable;

public class VentaDetalle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String producto_id;   // guardar como hex string
    private String nombre;
    private int cantidad = 1;     // default 1
    private double precio_unitario;
    private double subtotal;

    public String getProducto_id() { return producto_id; }
    public void setProducto_id(String producto_id) { this.producto_id = producto_id; }

    // Alias legacy
    public String getProductoId() { return producto_id; }
    public void setProductoId(String productoId) { this.producto_id = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
        recalcularSubtotal();
    }

    public double getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = Math.max(0d, precio_unitario);
        recalcularSubtotal();
    }

    public double getPrecioUnitario() { return precio_unitario; }
    public void setPrecioUnitario(double precioUnitario) {
        this.precio_unitario = Math.max(0d, precioUnitario);
        recalcularSubtotal();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = Math.max(0d, subtotal); }

    private void recalcularSubtotal() { this.subtotal = this.cantidad * this.precio_unitario; }

    @Override
    public String toString() {
        return "VentaDetalle{" +
                "producto_id='" + producto_id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precio_unitario=" + precio_unitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
