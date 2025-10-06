package com.refaccionaria.proveedoresInternos;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("devolucionesReportBean")
@SessionScoped
public class DevolucionesReportBean implements Serializable {

    private String fechaInicio;
    private String fechaFin;
    private String productoSeleccionado;

    private List<Map<String, Object>> listaDevoluciones;
    private List<Map<String, Object>> listaFiltrada;

    public DevolucionesReportBean() {
        cargarDatosSimulados();
    }

    // --- Datos simulados ---
    private void cargarDatosSimulados() {
        listaDevoluciones = new ArrayList<>();

        listaDevoluciones.add(Map.of(
                "id", "D001",
                "fecha", "2025-09-20",
                "producto", "Filtro de aire",
                "motivo", "Defecto de fábrica",
                "cantidad", "2",
                "estado", "Procesado"
        ));

        listaDevoluciones.add(Map.of(
                "id", "D002",
                "fecha", "2025-09-22",
                "producto", "Aceite sintético",
                "motivo", "Envase dañado",
                "cantidad", "1",
                "estado", "Pendiente"
        ));

        listaDevoluciones.add(Map.of(
                "id", "D003",
                "fecha", "2025-09-25",
                "producto", "Bujías NGK",
                "motivo", "Error en pedido",
                "cantidad", "4",
                "estado", "Reembolsado"
        ));

        listaDevoluciones.add(Map.of(
                "id", "D004",
                "fecha", "2025-09-28",
                "producto", "Filtro de aceite",
                "motivo", "Paño en transporte",
                "cantidad", "3",
                "estado", "Procesado"
        ));

        listaFiltrada = new ArrayList<>(listaDevoluciones);
    }

    // --- Acción del botón Consultar (simulada) ---
    public void consultar() {
        listaFiltrada = new ArrayList<>();

        for (Map<String, Object> devolucion : listaDevoluciones) {
            boolean coincide = true;

            // Filtrar por producto
            if (productoSeleccionado != null && !productoSeleccionado.isEmpty()) {
                coincide = devolucion.get("producto").equals(productoSeleccionado);
            }

            // (en el futuro puedes agregar aquí filtros por fechaInicio / fechaFin)
            if (coincide) {
                listaFiltrada.add(devolucion);
            }
        }
    }

    // --- Getters y Setters ---
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(String productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public List<Map<String, Object>> getListaDevoluciones() {
        return listaFiltrada;
    }

    public void setListaDevoluciones(List<Map<String, Object>> listaDevoluciones) {
        this.listaDevoluciones = listaDevoluciones;
    }
}
