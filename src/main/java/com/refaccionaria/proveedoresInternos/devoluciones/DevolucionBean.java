package com.refaccionaria.proveedoresInternos.devoluciones; // (Tu paquete)

// --- Imports ---
// Modelos (asumiendo tu paquete de modelos)
import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.models.VentaDetalle;
import com.refaccionaria.proveedoresInternos.models.Devolucion;
import com.refaccionaria.proveedoresInternos.models.DevolucionDetalle;
// Servicios (asumiendo tu paquete de servicios)
import com.refaccionaria.proveedoresInternos.devoluciones.VentaService;
import com.refaccionaria.proveedoresInternos.services.DevolucionService;
// JSF/Jakarta
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
// Java Utils
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.refaccionaria.proveedoresInternos.services.ActividadService;

@Named("DevolucionBean")
@ViewScoped
public class DevolucionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Servicios ---
    private VentaService ventaService;
    private DevolucionService devolucionService; // <-- CORRECCIÓN: Faltaba este servicio

    // --- Campos de Búsqueda ---
    private String folioVentaBusqueda;
    private Venta ventaEncontrada;
    private List<VentaDetalle> productosDeVenta;

    // --- Campos del Formulario de Devolución ---
    private String devolucionId;
    private Date fecha;
    private String motivo;
    private String estado;
    private String[] productosSeleccionados;

    @PostConstruct
    public void init() {
        this.fecha = new Date();
        // CORRECCIÓN: Estado inicial recomendado para el flujo
        this.estado = "Pendiente de Reembolso"; 
        this.productosDeVenta = new ArrayList<>();
        this.devolucionId = "DEV-" + (System.currentTimeMillis() / 1000);
        
        // CORRECCIÓN: Instanciar ambos servicios
        this.ventaService = new VentaService();
        this.devolucionService = new DevolucionService(); 
    }

    // --- Método de Búsqueda (Tu código estaba bien) ---
    public void buscarVenta() {
        this.ventaEncontrada = null;
        this.productosDeVenta.clear();
        this.productosSeleccionados = null;

        if (folioVentaBusqueda == null || folioVentaBusqueda.trim().isEmpty()) {
            addErrorMessage("Error", "Por favor, ingrese un folio de venta.");
            return;
        }
        this.ventaEncontrada = ventaService.buscarPorFolio(folioVentaBusqueda);

        if (this.ventaEncontrada != null) {
            this.productosDeVenta.addAll(this.ventaEncontrada.getDetalle());
            addInfoMessage("Venta Encontrada", "Se cargaron " + productosDeVenta.size() + " productos.");
        } else {
            addErrorMessage("No Encontrada", "No se encontró ninguna venta con el folio: " + folioVentaBusqueda);
        }
    }

    // --- ACCIÓN PRINCIPAL (CORREGIDA Y COMPLETADA) ---
    // Esta es la lógica que faltaba en tu versión
public String realizarDevolucion() {

    if (productosSeleccionados == null || productosSeleccionados.length == 0) {
        addErrorMessage("Error de Validación", "Debe seleccionar al menos un producto para devolver.");
        return null;
    }
    if (ventaEncontrada == null) {
        addErrorMessage("Error de Validación", "Debe buscar y cargar una venta válida primero.");
        return null;
    }

    Devolucion nuevaDevolucion = new Devolucion();
    nuevaDevolucion.setDevolucionId(this.devolucionId);
    nuevaDevolucion.setVentaId(this.ventaEncontrada.get_id());
    nuevaDevolucion.setFolioVenta(this.ventaEncontrada.getFolio_venta());
    nuevaDevolucion.setFecha(new Date());
    nuevaDevolucion.setMotivo(this.motivo);
    nuevaDevolucion.setEstado(this.estado);

    List<DevolucionDetalle> detalleDevolucion = new ArrayList<>();
    double totalReembolsadoCalculado = 0.0;

    for (String idProductoSeleccionado : productosSeleccionados) {
        for (VentaDetalle itemVenta : ventaEncontrada.getDetalle()) {
            if (itemVenta.getProducto_id().equals(idProductoSeleccionado)) {
                totalReembolsadoCalculado += itemVenta.getSubtotal();

                DevolucionDetalle det = new DevolucionDetalle();
                det.setProducto_id(itemVenta.getProducto_id());
                det.setNombre(itemVenta.getNombre());
                det.setCantidadDevuelta(itemVenta.getCantidad());
                det.setPrecioUnitario(itemVenta.getPrecio_unitario());
                det.setSubtotalDevuelto(itemVenta.getSubtotal());
                detalleDevolucion.add(det);
                break;
            }
        }
    }

    nuevaDevolucion.setDetalle(detalleDevolucion);
    nuevaDevolucion.setTotalReembolsado(totalReembolsadoCalculado);

    boolean exito = devolucionService.registrarDevolucion(nuevaDevolucion);

    if (exito) {
        // 🔹 Registrar actividad en historial
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Object userObj = ctx.getExternalContext().getSessionMap().get("usuarioActual");
            String usuarioId = "desconocido";

if (userObj instanceof com.refaccionaria.proveedoresInternos.models.Usuario) {
    com.refaccionaria.proveedoresInternos.models.Usuario u =
        (com.refaccionaria.proveedoresInternos.models.Usuario) userObj;
    if (u.getUsuario() != null) {
        usuarioId = u.getUsuario();
    }
}


            ActividadService actividadService = new ActividadService();
            actividadService.registrarActividad(
                "Devolución registrada - Folio " + nuevaDevolucion.getDevolucionId(),
                usuarioId
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        addInfoMessage("Devolución Registrada",
            "Folio " + nuevaDevolucion.getDevolucionId() + " guardado. Stock actualizado.");
        return "menu_devoluciones?faces-redirect=true";
    } else {
        addErrorMessage("Error en Base de Datos", "No se pudo guardar la devolución. Revise los logs.");
        return null;
    }
}


    public String cancelar() {
        return "menu_devoluciones?faces-redirect=true";
    }

    // --- Métodos de Ayuda para Mensajes (Tu código estaba bien) ---
    private void addInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    // --- Getters y Setters (Tu código estaba bien) ---
    public String getFolioVentaBusqueda() {
        return folioVentaBusqueda;
    }

    public void setFolioVentaBusqueda(String folioVentaBusqueda) {
        this.folioVentaBusqueda = folioVentaBusqueda;
    }

    public Venta getVentaEncontrada() {
        return ventaEncontrada;
    }

    public void setVentaEncontrada(Venta ventaEncontrada) {
        this.ventaEncontrada = ventaEncontrada;
    }

    public List<VentaDetalle> getProductosDeVenta() {
        return productosDeVenta;
    }

    public void setProductosDeVenta(List<VentaDetalle> productosDeVenta) {
        this.productosDeVenta = productosDeVenta;
    }

    public String getDevolucionId() {
        return devolucionId;
    }

    public void setDevolucionId(String devolucionId) {
        this.devolucionId = devolucionId;
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

    public String[] getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public void setProductosSeleccionados(String[] productosSeleccionados) {
        this.productosSeleccionados = productosSeleccionados;
    }
}