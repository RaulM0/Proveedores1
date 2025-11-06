package com.refaccionaria.proveedoresInternos.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String _id;              // String del ObjectId
    private String folio_venta;
    private Date fecha_venta;
    private String usuario_id;       // puede guardar username o el ObjectId.toHexString()
    private Double total;            // usar Wrapper para evitar NPE en conversiones
    private String status;
    private List<VentaDetalle> detalle;

    // --- Getters y Setters ---
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getFolio_venta() { return folio_venta; }
    public void setFolio_venta(String folio_venta) { this.folio_venta = folio_venta; }

    public Date getFecha_venta() { return fecha_venta; }
    public void setFecha_venta(Date fecha_venta) { this.fecha_venta = fecha_venta; }

    public String getUsuario_id() { return usuario_id; }
    public void setUsuario_id(String usuario_id) { this.usuario_id = usuario_id; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ✅ Inicialización segura de lista para evitar null en las vistas JSF
    public List<VentaDetalle> getDetalle() {
        if (detalle == null) {
            detalle = new ArrayList<>();
        }
        return detalle;
    }
    public void setDetalle(List<VentaDetalle> detalle) {
        this.detalle = detalle;
    }

    // ✅ toString útil para depuración
    @Override
    public String toString() {
        return "Venta{" +
                "folio_venta='" + folio_venta + '\'' +
                ", fecha_venta=" + fecha_venta +
                ", usuario_id='" + usuario_id + '\'' +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", detalle=" + (detalle != null ? detalle.size() : 0) + " productos}";
    }
}
