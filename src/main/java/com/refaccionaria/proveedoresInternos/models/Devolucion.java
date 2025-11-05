package com.refaccionaria.proveedoresInternos.models; // (O el paquete donde pongas tus modelos)

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Representa el documento principal que se guardará 
 * en la nueva colección 'devoluciones' de MongoDB.
 */
public class Devolucion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _id; // (MongoDB lo generará, pero lo mapeamos)
    
    // --- Datos de Identificación ---
    private String devolucionId; // El folio legible "DEV-..." que generamos
    private String ventaId;      // El ObjectId de la venta original
    private String folioVenta;   // El folio legible "V-2025-..."

    // --- Datos de la Devolución ---
    private Date fecha;          // Fecha en que se registra la devolución
    private String motivo;       // Motivo escrito por el usuario
    private String estado;       // "Pendiente de Reembolso", "Completada", etc.

    // --- Datos Financieros y de Productos ---
    private List<DevolucionDetalle> detalle; // Los productos devueltos
    private double totalReembolsado;       // La suma de los subtotales del detalle

    
    // --- Getters y Setters ---

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDevolucionId() {
        return devolucionId;
    }

    public void setDevolucionId(String devolucionId) {
        this.devolucionId = devolucionId;
    }

    public String getVentaId() {
        return ventaId;
    }

    public void setVentaId(String ventaId) {
        this.ventaId = ventaId;
    }

    public String getFolioVenta() {
        return folioVenta;
    }

    public void setFolioVenta(String folioVenta) {
        this.folioVenta = folioVenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DevolucionDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DevolucionDetalle> detalle) {
        this.detalle = detalle;
    }

    public double getTotalReembolsado() {
        return totalReembolsado;
    }

    public void setTotalReembolsado(double totalReembolsado) {
        this.totalReembolsado = totalReembolsado;
    }
}