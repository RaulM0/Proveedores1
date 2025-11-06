package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.devoluciones.VentaService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("consultaVentasBean")
@SessionScoped
public class ConsultaVentasBean implements Serializable {
    private List<Venta> listaVentas;
    private Venta ventaSeleccionada;
    private String folioBusqueda;
    private String estadoFiltro;
    private Date fechaInicio;

    private transient VentaService ventaService;

    @PostConstruct
    public void init() {
        ventaService = new VentaService();
        buscarVentas();
    }

    public void buscarVentas() {
        listaVentas = ventaService.buscarVentas(folioBusqueda, estadoFiltro, fechaInicio);
    }

    public void verDetalle(Venta venta) {
        this.ventaSeleccionada = venta;
    }

    // Getters / Setters
    public List<Venta> getListaVentas() { return listaVentas; }
    public Venta getVentaSeleccionada() { return ventaSeleccionada; }
    public void setVentaSeleccionada(Venta v) { this.ventaSeleccionada = v; }
    public String getFolioBusqueda() { return folioBusqueda; }
    public void setFolioBusqueda(String folioBusqueda) { this.folioBusqueda = folioBusqueda; }
    public String getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(String estadoFiltro) { this.estadoFiltro = estadoFiltro; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
}
