
package com.refaccionaria.proveedoresInternos.models;

import java.util.Date;

/**
 *
 * @author RMD
 */
public class Devolucion {
    
    private String id;
    private String ventaId;
    private String productoId;
    private int cantidad;
    private String motivo;
    private Date fecha;
    private String estado;   // Pendiente, Aprobada, Rechazada, Procesada

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVentaId() {
        return ventaId;
    }

    public void setVentaId(String ventaId) {
        this.ventaId = ventaId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
