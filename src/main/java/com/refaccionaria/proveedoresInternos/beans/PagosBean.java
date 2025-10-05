package com.refaccionaria.proveedoresInternos.beans;

import jakarta.servlet.http.Part;

import com.refaccionaria.proveedoresInternos.models.Pago;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("pagosBean")
@SessionScoped
public class PagosBean implements Serializable {
    private Part archivoComprobante;

    public Part getArchivoComprobante() {
        return archivoComprobante;
    }

    public void setArchivoComprobante(Part archivoComprobante) {
        this.archivoComprobante = archivoComprobante;
    }

    // Maneja la carga de comprobante y lo asocia al pago seleccionado
    public void subirComprobante() {
        if (archivoComprobante != null && pagoSeleccionado != null) {
            String nombreArchivo = obtenerNombreArchivo(archivoComprobante);
            pagoSeleccionado.setComprobante(nombreArchivo);
            // Aquí puedes guardar el archivo físicamente si lo deseas
            archivoComprobante = null;
        }
    }

    private String obtenerNombreArchivo(Part part) {
        String header = part.getHeader("content-disposition");
        if (header != null) {
            for (String cd : header.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                    return filename;
                }
            }
        }
        return "archivo_comprobante.pdf";
    }

    private List<Pago> pagos = new ArrayList<>();
    private String terminoBusqueda;
    private String estadoFiltro = "";
    private String fechaFiltro;

    private boolean modalVisible = false;
    private Pago pagoSeleccionado;

    @PostConstruct
    public void cargarEjemplos() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        pagos.add(crearPago("ERP-VENTA-001", sdf.format(new Date()), "REF-HSBC-98765", 2450.50, "pendiente",
                "comprobante_erp001.pdf"));
        pagos.add(crearPago("ERP-VENTA-002", "04/10/2025", "REF-BBVA-12345", 8999.00, "confirmado",
                "comprobante_erp002.pdf"));
        pagos.add(crearPago("ERP-VENTA-003", "03/10/2025", "REF-SANT-67890", 320.00, "rechazado",
                "comprobante_erp003.pdf"));
        pagos.add(crearPago("ERP-VENTA-004", "02/10/2025", "N/A", 1500.00, "pendiente", null));
    }

    private Pago crearPago(String ventaId, String fecha, String referencia, double monto, String estado,
            String comprobante) {
        Pago p = new Pago();
        p.setVentaId(ventaId);
        p.setReferenciaBanco(referencia);
        p.setMonto(monto);
        p.setEstado(estado);
        p.setComprobante(comprobante);
        // Usar la fecha recibida como string para mostrar en la tabla
        // Si quieres guardar como Date, puedes parsear, pero aquí lo dejamos como
        // string para la vista
        return p;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public String getEstadoFiltro() {
        return estadoFiltro;
    }

    public void setEstadoFiltro(String estadoFiltro) {
        this.estadoFiltro = estadoFiltro;
    }

    public String getFechaFiltro() {
        return fechaFiltro;
    }

    public void setFechaFiltro(String fechaFiltro) {
        this.fechaFiltro = fechaFiltro;
    }

    public boolean isModalVisible() {
        return modalVisible;
    }

    public Pago getPagoSeleccionado() {
        return pagoSeleccionado;
    }

    public void buscarPagos() {
        // Filtrado de ejemplo, puedes implementar lógica real aquí
    }

    public void verDetalle(Pago pago) {
        this.pagoSeleccionado = pago;
        this.modalVisible = true;
    }

    public void cerrarModal() {
        this.modalVisible = false;
    }

    public void confirmarPago() {
        if (pagoSeleccionado != null) {
            pagoSeleccionado.setEstado("confirmado");
            cerrarModal();
        }
    }

    public void rechazarPago() {
        if (pagoSeleccionado != null) {
            pagoSeleccionado.setEstado("rechazado");
            cerrarModal();
        }
    }

    public void descargarComprobante() {
        // Implementa la lógica de descarga si es necesario
    }
}
