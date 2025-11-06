
package com.refaccionaria.proveedoresInternos.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Modelo para pagos asociados a ventas
 * 
 * @author RMD
 */
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id; // _id de MongoDB
    private String ventaId; // ObjectId de la venta
    private String folioVenta; // Folio de la venta (para referencia)
    private Date fecha;
    private String referenciaBanco;
    private double monto;
    private String metodoPago; // Efectivo, Transferencia, Tarjeta
    private String estado; // Completado, Pendiente, Cancelado
    private String comprobante;

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getFolioVenta() {
        return folioVenta;
    }

    public void setFolioVenta(String folioVenta) {
        this.folioVenta = folioVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getReferenciaBanco() {
        return referenciaBanco;
    }

    public void setReferenciaBanco(String referenciaBanco) {
        this.referenciaBanco = referenciaBanco;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
