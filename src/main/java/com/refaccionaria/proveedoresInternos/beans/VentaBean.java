package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.models.VentaDetalle;
import com.refaccionaria.proveedoresInternos.devoluciones.VentaService;
import com.refaccionaria.proveedoresInternos.services.ProductoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("ventaBean")
@SessionScoped
public class VentaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Venta nuevaVenta;
    private VentaDetalle detalleAuxiliar;

    private double subtotal;
    private double iva;
    private double total;
    private double montoPagado;
    private double cambio;

    private String productoABuscarId;

    // Servicios
    private transient ProductoService productoService;
    private transient VentaService ventaService;

    @PostConstruct

public void inicializarVenta() {
    nuevaVenta = new Venta();
    nuevaVenta.setFecha_venta(new Date());
    nuevaVenta.setStatus("Pendiente");
    nuevaVenta.setDetalle(new ArrayList<>());
    detalleAuxiliar = new VentaDetalle();
    productoService = new ProductoService();
    ventaService = new VentaService();

    // ✅ Leer usuario actual de sesión (sin modificar LoginBean)
    try {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Object userObj = ctx.getExternalContext().getSessionMap().get("usuarioActual");
        if (userObj != null && userObj instanceof com.refaccionaria.proveedoresInternos.models.Usuario) {
            com.refaccionaria.proveedoresInternos.models.Usuario u =
                    (com.refaccionaria.proveedoresInternos.models.Usuario) userObj;
            if (u.getUsuario() != null) {
                nuevaVenta.setUsuario_id(u.getUsuario()); // ej: "admin01"
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        nuevaVenta.setUsuario_id("desconocido");
    }

    recalcularTotales();
}

    public void buscarProducto() {
        try {
            Document producto = productoService.buscarProducto(productoABuscarId);
            if (producto != null) {
                detalleAuxiliar = new VentaDetalle();
                Object id = producto.get("_id");
                if (id != null) {
                    if (id instanceof org.bson.types.ObjectId) {
                        detalleAuxiliar.setProducto_id(((org.bson.types.ObjectId) id).toHexString());
                    } else {
                        detalleAuxiliar.setProducto_id(String.valueOf(id));
                    }
                }
                detalleAuxiliar.setNombre(producto.getString("nombre"));

                Double precioVenta = null;
                Object raw = producto.get("precioVenta");
                if (raw instanceof Number) precioVenta = ((Number) raw).doubleValue();

                if (precioVenta == null) {
                    addWarnMessage("Atención", "El producto no tiene precioVenta definido.");
                    return;
                }

                detalleAuxiliar.setPrecio_unitario(precioVenta);
                detalleAuxiliar.setCantidad(1); // setter recalcula subtotal

                addInfoMessage("Producto encontrado",
                        "Se cargó " + detalleAuxiliar.getNombre() + " ($" + precioVenta + ")");
            } else {
                addWarnMessage("Sin resultados", "No se encontró el producto solicitado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Error", "No se pudo buscar el producto.");
        }
    }

    public void agregarProductoALaVenta() {
        if (detalleAuxiliar.getProducto_id() == null) {
            addWarnMessage("Atención", "Primero busca un producto.");
            return;
        }
        // asegurar subtotal consistente
        detalleAuxiliar.setSubtotal(detalleAuxiliar.getCantidad() * detalleAuxiliar.getPrecio_unitario());
        nuevaVenta.getDetalle().add(detalleAuxiliar);
        recalcularTotales();
        limpiarDetalleAuxiliar();
        addInfoMessage("Producto agregado", "Se añadió a la venta.");
    }

    public void eliminarProducto(VentaDetalle detalle) {
    if (nuevaVenta != null && nuevaVenta.getDetalle() != null) {
        nuevaVenta.getDetalle().remove(detalle);
        recalcularTotales();
        addInfoMessage("Producto eliminado", "Se eliminó de la venta correctamente.");
    }
}


    public void limpiarDetalleAuxiliar() { detalleAuxiliar = new VentaDetalle(); }

    public void recalcularTotales() {
        subtotal = 0d;
        if (nuevaVenta.getDetalle() != null) {
            for (VentaDetalle d : nuevaVenta.getDetalle()) {
                // asegurar que subtotal == cantidad * precio en edición
                d.setSubtotal(d.getCantidad() * d.getPrecio_unitario());
                subtotal += d.getSubtotal();
            }
        }
        iva = subtotal * 0.16;
        total = subtotal + iva;
        cambio = montoPagado - total;
    }

public void registrarVenta() {
    try {
        recalcularTotales();
        nuevaVenta.setTotal(total);
        nuevaVenta.setStatus("Completada");
        nuevaVenta.setFecha_venta(new Date());

        // ✅ Obtener usuario actual de sesión (guardado por LoginBean)
        FacesContext ctx = FacesContext.getCurrentInstance();
        Object userObj = ctx.getExternalContext().getSessionMap().get("usuarioActual");
        String usuarioId = "desconocido";

        try {
            if (userObj != null) {
                if (userObj instanceof com.refaccionaria.proveedoresInternos.models.Usuario) {
                    com.refaccionaria.proveedoresInternos.models.Usuario u =
                            (com.refaccionaria.proveedoresInternos.models.Usuario) userObj;
                    if (u.getUsuario() != null) {
                        usuarioId = u.getUsuario();
                    }
                } else if (userObj instanceof org.bson.Document) {
                    org.bson.Document doc = (org.bson.Document) userObj;
                    Object u = doc.get("usuario");
                    if (u != null) usuarioId = u.toString();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        nuevaVenta.setUsuario_id(usuarioId);

        // ✅ Asignar folio automáticamente
        String nuevoFolio = ventaService.generarFolio();
        nuevaVenta.setFolio_venta(nuevoFolio);

        // ✅ Guardar venta en Mongo
        ventaService.insertarVenta(nuevaVenta);

        // ✅ Registrar la actividad en historial
        com.refaccionaria.proveedoresInternos.services.ActividadService actividadService =
                new com.refaccionaria.proveedoresInternos.services.ActividadService();
        actividadService.registrarActividad(
                "Venta registrada - Folio " + nuevoFolio, usuarioId);

        addInfoMessage("Venta registrada",
                "Se guardó correctamente por el usuario: " + nuevaVenta.getUsuario_id());

        inicializarVenta();

    } catch (Exception e) {
        e.printStackTrace();
        addErrorMessage("Error", "No se pudo registrar la venta.");
    }
}



    // Mensajes
    private void addInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addWarnMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    // Getters & Setters
    public Venta getNuevaVenta() { return nuevaVenta; }
    public VentaDetalle getDetalleAuxiliar() { return detalleAuxiliar; }
    public void setDetalleAuxiliar(VentaDetalle detalleAuxiliar) { this.detalleAuxiliar = detalleAuxiliar; }
    public double getSubtotal() { return subtotal; }
    public double getIva() { return iva; }
    public double getTotal() { return total; }
    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; recalcularTotales(); }
    public double getCambio() { return cambio; }
    public String getProductoABuscarId() { return productoABuscarId; }
    public void setProductoABuscarId(String productoABuscarId) { this.productoABuscarId = productoABuscarId; }
}
