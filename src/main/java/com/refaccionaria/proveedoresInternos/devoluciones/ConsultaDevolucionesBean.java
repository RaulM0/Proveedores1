package com.refaccionaria.proveedoresInternos.devoluciones;

import com.refaccionaria.proveedoresInternos.models.Devolucion;
import com.refaccionaria.proveedoresInternos.services.DevolucionService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

@Named("consultaDevolucionesBean")
@ViewScoped
public class ConsultaDevolucionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Filtros de Búsqueda ---
    private String filtroFolio;
    private String filtroEstado;
    private String filtroFechaDesde; // Usamos String para el input
    private String filtroFechaHasta; // Usamos String para el input

    // --- Resultados ---
    private List<Devolucion> resultados;

    // --- Servicios ---
    private DevolucionService devolucionService;

    @PostConstruct
    public void init() {
        this.devolucionService = new DevolucionService();
        this.resultados = new ArrayList<>();
        // Carga inicial de todos los registros (o los más recientes)
        buscar();
    }

    /**
     * Método principal llamado por el botón "Buscar". Prepara los filtros y
     * llama al servicio.
     */
    public void buscar() {
        Date fechaDesde = null;
        Date fechaHasta = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // --- Conversión y Validación de Fechas ---
        try {
            if (filtroFechaDesde != null && !filtroFechaDesde.isEmpty()) {
                fechaDesde = sdf.parse(filtroFechaDesde);
            }
            if (filtroFechaHasta != null && !filtroFechaHasta.isEmpty()) {
                fechaHasta = sdf.parse(filtroFechaHasta);
            }
        } catch (ParseException e) {
            addErrorMessage("Error de Fecha", "El formato de fecha debe ser dd/mm/aaaa.");
            return; // Detiene la búsqueda si el formato es incorrecto
        }

        // --- Llamada al Servicio ---
        try {
            this.resultados = devolucionService.buscarDevoluciones(
                    filtroFolio,
                    filtroEstado,
                    fechaDesde,
                    fechaHasta
            );

            if (this.resultados.isEmpty()) {
                addInfoMessage("Sin Resultados", "No se encontraron devoluciones con esos criterios.");
            }

        } catch (Exception e) {
            addErrorMessage("Error de Búsqueda", "Ocurrió un error al consultar la base de datos.");
            e.printStackTrace();
        }
    }

    /**
     * Limpia todos los campos de filtro y vuelve a cargar la tabla.
     */
    public void limpiarFiltros() {
        this.filtroFolio = null;
        this.filtroEstado = null;
        this.filtroFechaDesde = null;
        this.filtroFechaHasta = null;
        // Llama a buscar() para refrescar la lista (ahora sin filtros)
        buscar();
    }

    /**
     * Helper para el CSS. Convierte "Procesada" en "procesada" o "Pendiente de
     * Reembolso" en "pendiente".
     */
    public String normalizarEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            return "desconocido";
        }
        // Toma la primera palabra y la pone en minúsculas
        return estado.split(" ")[0].toLowerCase();
    }

    // --- Getters y Setters para los filtros y resultados ---
    public String getFiltroFolio() {
        return filtroFolio;
    }

    public void setFiltroFolio(String filtroFolio) {
        this.filtroFolio = filtroFolio;
    }

    public String getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }

    public String getFiltroFechaDesde() {
        return filtroFechaDesde;
    }

    public void setFiltroFechaDesde(String filtroFechaDesde) {
        this.filtroFechaDesde = filtroFechaDesde;
    }

    public String getFiltroFechaHasta() {
        return filtroFechaHasta;
    }

    public void setFiltroFechaHasta(String filtroFechaHasta) {
        this.filtroFechaHasta = filtroFechaHasta;
    }

    public List<Devolucion> getResultados() {
        return resultados;
    }

    public void setResultados(List<Devolucion> resultados) {
        this.resultados = resultados;
    }

    // --- Métodos de ayuda para mensajes ---
    private void addInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
}
