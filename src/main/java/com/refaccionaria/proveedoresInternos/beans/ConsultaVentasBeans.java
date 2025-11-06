package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Venta;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("consultaVentasBean")
@ViewScoped
public class ConsultaVentasBeans implements Serializable {

    private List<Venta> listaVentas;
    
    // Propiedades para filtros
    private String folioBusqueda;
    private Date fechaInicio;
    private Date fechaFin;
    private String estadoFiltro;
    
    private Venta ventaSeleccionada;

    public ConsultaVentasBeans() {
        this.listaVentas = new ArrayList<>();
        // Al inicializar el bean, cargamos los datos (simulación)
        buscarVentas(); 
    }

    /**
     * Realiza la búsqueda de ventas aplicando los filtros.
     * En una aplicación real, esto llama a un servicio de JPA/BD.
     */
    public void buscarVentas() {
        // En un entorno real, aquí se construiría una consulta dinámica.
        
        // --- SIMULACIÓN DE DATOS ---
        this.listaVentas.clear();
        
        Venta v1 = new Venta();
        v1.setFolioVenta("V-00101");
        v1.setFechaVenta(new Date(System.currentTimeMillis() - 86400000)); // Ayer
        v1.setTotal(450.50);
        v1.setStatus("Completada");
        
        Venta v2 = new Venta();
        v2.setFolioVenta("V-00102");
        v2.setFechaVenta(new Date()); // Hoy
        v2.setTotal(1200.00);
        v2.setStatus("Pendiente");

        this.listaVentas.add(v1);
        this.listaVentas.add(v2);
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Mostrando " + listaVentas.size() + " ventas."));
    }

    // --- Getters y Setters ---

    public List<Venta> getListaVentas() {
        return listaVentas;
    }
    
    public void seleccionarVenta(Venta venta) {
    this.ventaSeleccionada = venta;
    
    // Ejecutar JavaScript para mostrar el diálogo.
    // El nombre 'PF' se usa para acceder a la API de PrimeFaces en JS.
    org.primefaces.PrimeFaces.current().executeScript("PF('detalleVentaDialog').show()");
}

    // Getters/Setters para los filtros (necesarios para los inputs del XHTML)
    public String getFolioBusqueda() { return folioBusqueda; }
    public void setFolioBusqueda(String folioBusqueda) { this.folioBusqueda = folioBusqueda; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public String getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(String estadoFiltro) { this.estadoFiltro = estadoFiltro;}
    public Venta getVentaSeleccionada() {return ventaSeleccionada;}
    public void setVentaSeleccionada(Venta ventaSeleccionada) {this.ventaSeleccionada = ventaSeleccionada;}
}