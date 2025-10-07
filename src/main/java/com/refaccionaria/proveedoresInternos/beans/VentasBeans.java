package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.models.DetalleVenta;
import com.refaccionaria.proveedoresInternos.models.HistorialVenta;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("ventaBean")
@ViewScoped
public class VentasBeans implements Serializable {

    private Venta nuevaVenta;
    private DetalleVenta detalleAuxiliar;
    private String productoABuscarId; 
    private double montoPagado;
    private double cambio;

    public VentasBeans() {
        inicializarVenta();
    }

    public void inicializarVenta() {
        this.nuevaVenta = new Venta();
        this.nuevaVenta.setDetalle(new ArrayList<>());
        this.nuevaVenta.setFechaVenta(new Date());
        this.nuevaVenta.setStatus("Pendiente");
        
        this.detalleAuxiliar = new DetalleVenta();
        this.productoABuscarId = null;
        this.montoPagado = 0.0;
        this.cambio = 0.0;
        // Asumiendo que Venta tiene setSubtotal/setIva/setTotal, si no, se calcularán en calcularTotales()
    }

    public void buscarProducto() {
        // ... (Tu lógica de búsqueda simulada) ...
    }
    
    public void agregarProductoALaVenta() {
        if (detalleAuxiliar == null || detalleAuxiliar.getProductoId() == null || detalleAuxiliar.getCantidad() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Faltan datos del producto."));
            return;
        }

        double subtotal = detalleAuxiliar.getCantidad() * detalleAuxiliar.getPrecioUnitario();
        detalleAuxiliar.setSubtotal(subtotal);

        // ¡CORRECCIÓN CRÍTICA DE REFERENCIA!: Añadir una nueva instancia antes de limpiarla
        DetalleVenta itemFinal = new DetalleVenta();
        // Asignación de propiedades... (Requiere constructor de copia o asignación manual)
        itemFinal.setProductoId(detalleAuxiliar.getProductoId());
        itemFinal.setNombre(detalleAuxiliar.getNombre());
        itemFinal.setCantidad(detalleAuxiliar.getCantidad());
        itemFinal.setPrecioUnitario(detalleAuxiliar.getPrecioUnitario());
        itemFinal.setSubtotal(detalleAuxiliar.getSubtotal());
        
        this.nuevaVenta.getDetalle().add(itemFinal);

        calcularTotales();
        
        this.detalleAuxiliar = new DetalleVenta(); // Limpiar el auxiliar
        this.productoABuscarId = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Añadido", "Producto agregado."));
    }

    public void calcularTotales() {
        double totalSubtotal = 0.0;
        for (DetalleVenta detalle : nuevaVenta.getDetalle()) {
            totalSubtotal += detalle.getSubtotal();
        }

        double ivaTasa = 0.16;
        double totalIva = totalSubtotal * ivaTasa;
        double totalFinal = totalSubtotal + totalIva;

        // Establecer totales en el objeto Venta (asumiendo que tiene los setters necesarios)
        // nuevaVenta.setSubtotal(totalSubtotal);
        // nuevaVenta.setIva(totalIva);
        nuevaVenta.setTotal(totalFinal); 

        this.cambio = (this.montoPagado >= totalFinal) ? this.montoPagado - totalFinal : 0.0;
    }

    public void eliminarProducto(DetalleVenta detalle) {
        nuevaVenta.getDetalle().remove(detalle);
        calcularTotales();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Producto eliminado."));
    }
    
    public String guardarVenta() {
        // ... (Lógica de guardado) ...
        inicializarVenta();
        return null;
    }
    
    // --- GETTERS COMPLEMENTARIOS PARA XHTML (Evitan PropertyNotFoundException) ---
    // ESTOS GETTERS ACCEDEN A LOS TOTALES CALCULADOS Y SON ACCEDIDOS COMO ventaBean.subtotal
    public double getSubtotal() {
        // Calcula o retorna el subtotal de la Venta para el XHTML
        return this.nuevaVenta.getTotal() / 1.16; // Aproximación inversa del IVA
    }

    public double getIva() {
        // Retorna el IVA para el XHTML
        return this.nuevaVenta.getTotal() - this.getSubtotal();
    }
    
    public double getTotal() {
        // Retorna el total ya calculado
        return this.nuevaVenta.getTotal();
    }
    
    public void limpiarDetalleAuxiliar() {
    this.detalleAuxiliar = new com.refaccionaria.proveedoresInternos.models.DetalleVenta();
    }
    
    // ... (El resto de tus Getters/Setters) ...
    public Venta getNuevaVenta() { return nuevaVenta; }
    public DetalleVenta getDetalleAuxiliar() { return detalleAuxiliar; }
    public String getProductoABuscarId() { return productoABuscarId; }
    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; calcularTotales(); }
    public double getCambio() { return cambio; }
}