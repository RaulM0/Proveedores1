package com.refaccionaria.proveedoresInternos.beans;

import jakarta.servlet.http.Part;
import com.refaccionaria.proveedoresInternos.models.Pago;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

@Named("pagosBean")
@SessionScoped
public class PagosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Part archivoComprobante;
    private List<Pago> pagos = new ArrayList<>();
    private List<Pago> pagosFiltrados = new ArrayList<>();
    private String terminoBusqueda = "";
    private String estadoFiltro = "";
    private String fechaFiltro;
    private boolean modalVisible = false;
    private Pago pagoSeleccionado;

    @PostConstruct
    public void init() {
        cargarPagosDesdeMongo();
        actualizarFiltrados();
    }

    private MongoCollection<Document> getPagosCollection() {
        try {
            MongoDatabase db = LoginBean.getDatabase();
            return db.getCollection("pagos");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cargarPagosDesdeMongo() {
        pagos.clear();
        MongoCollection<Document> coll = getPagosCollection();
        if (coll == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de conexión",
                            "No se pudo conectar a MongoDB."));
            return;
        }

        try (MongoCursor<Document> cursor = coll.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Pago pago = new Pago();
                pago.setId(doc.getObjectId("_id").toString());

                Object ventaIdObj = doc.get("ventaId");
                if (ventaIdObj != null) {
                    pago.setVentaId(ventaIdObj.toString());
                }

                pago.setFolioVenta(doc.getString("folioVenta"));
                pago.setFecha(doc.getDate("fecha"));
                pago.setReferenciaBanco(doc.getString("referenciaBanco"));

                Object montoObj = doc.get("monto");
                if (montoObj instanceof Number) {
                    pago.setMonto(((Number) montoObj).doubleValue());
                }

                pago.setMetodoPago(doc.getString("metodoPago"));
                pago.setEstado(doc.getString("estado"));
                pago.setComprobante(doc.getString("comprobante"));

                pagos.add(pago);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al cargar pagos: " + e.getMessage()));
        }
    }

    private void actualizarFiltrados() {
        pagosFiltrados.clear();

        for (Pago pago : pagos) {
            boolean coincide = true;

            // Filtro por término de búsqueda (busca en ventaId, folioVenta y
            // referenciaBanco)
            if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
                String termino = terminoBusqueda.toLowerCase();
                boolean encontrado = false;

                // Búsqueda en ventaId
                if (pago.getVentaId() != null && pago.getVentaId().toLowerCase().contains(termino)) {
                    encontrado = true;
                }
                // Búsqueda en folioVenta
                else if (pago.getFolioVenta() != null && pago.getFolioVenta().toLowerCase().contains(termino)) {
                    encontrado = true;
                }
                // Búsqueda en referenciaBanco
                else if (pago.getReferenciaBanco() != null
                        && pago.getReferenciaBanco().toLowerCase().contains(termino)) {
                    encontrado = true;
                }

                coincide = encontrado;
            }

            // Filtro por estado
            if (coincide && estadoFiltro != null && !estadoFiltro.isEmpty()) {
                coincide = estadoFiltro.equalsIgnoreCase(pago.getEstado());
            }

            // Filtro por fecha
            if (coincide && fechaFiltro != null && !fechaFiltro.isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date fechaSeleccionada = sdf.parse(fechaFiltro);

                    if (pago.getFecha() != null) {
                        // Comparar solo la fecha (ignorar horas)
                        java.util.Calendar calFiltro = java.util.Calendar.getInstance();
                        calFiltro.setTime(fechaSeleccionada);
                        calFiltro.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calFiltro.set(java.util.Calendar.MINUTE, 0);
                        calFiltro.set(java.util.Calendar.SECOND, 0);
                        calFiltro.set(java.util.Calendar.MILLISECOND, 0);

                        java.util.Calendar calPago = java.util.Calendar.getInstance();
                        calPago.setTime(pago.getFecha());
                        calPago.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calPago.set(java.util.Calendar.MINUTE, 0);
                        calPago.set(java.util.Calendar.SECOND, 0);
                        calPago.set(java.util.Calendar.MILLISECOND, 0);

                        coincide = calFiltro.getTimeInMillis() == calPago.getTimeInMillis();
                    } else {
                        coincide = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Si hay error al parsear la fecha, ignorar este filtro
                }
            }

            if (coincide) {
                pagosFiltrados.add(pago);
            }
        }
    }

    public void buscarPagos() {
        actualizarFiltrados();

        // Mensaje informativo sobre resultados
        int resultados = pagosFiltrados.size();
        if (resultados == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin resultados",
                            "No se encontraron pagos con los criterios especificados."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Búsqueda exitosa",
                            "Se encontraron " + resultados + " pago(s)."));
        }
    }

    public void limpiarFiltros() {
        terminoBusqueda = "";
        estadoFiltro = "";
        fechaFiltro = null;
        actualizarFiltrados();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Filtros limpiados",
                        "Se muestran todos los pagos (" + pagosFiltrados.size() + ")."));
    }

    public void verDetalle(Pago pago) {
        this.pagoSeleccionado = pago;
        this.modalVisible = true;
    }

    public void cerrarModal() {
        this.modalVisible = false;
        this.pagoSeleccionado = null;
    }

    public void confirmarPago() {
        if (pagoSeleccionado != null) {
            pagoSeleccionado.setEstado("Completado");
            actualizarPagoEnDB(pagoSeleccionado);
            actualizarFiltrados();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Pago confirmado",
                            "El pago ha sido confirmado exitosamente."));
            cerrarModal();
        }
    }

    public void rechazarPago() {
        if (pagoSeleccionado != null) {
            pagoSeleccionado.setEstado("Cancelado");
            actualizarPagoEnDB(pagoSeleccionado);
            actualizarFiltrados();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Pago rechazado",
                            "El pago ha sido rechazado."));
            cerrarModal();
        }
    }

    private void actualizarPagoEnDB(Pago pago) {
        MongoCollection<Document> coll = getPagosCollection();
        if (coll == null)
            return;

        try {
            Document doc = new Document("estado", pago.getEstado());
            if (pago.getComprobante() != null) {
                doc.append("comprobante", pago.getComprobante());
            }

            coll.updateOne(
                    new Document("_id", new org.bson.types.ObjectId(pago.getId())),
                    new Document("", doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subirComprobante() {
        if (archivoComprobante != null && pagoSeleccionado != null) {
            String nombreArchivo = obtenerNombreArchivo(archivoComprobante);
            pagoSeleccionado.setComprobante(nombreArchivo);
            actualizarPagoEnDB(pagoSeleccionado);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Comprobante cargado",
                            "El comprobante se ha asociado al pago."));
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
        return "comprobante_" + System.currentTimeMillis() + ".pdf";
    }

    public void descargarComprobante() {
        if (pagoSeleccionado != null && pagoSeleccionado.getComprobante() != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Descarga",
                            "Descargando: " + pagoSeleccionado.getComprobante()));
        }
    }

    public Part getArchivoComprobante() {
        return archivoComprobante;
    }

    public void setArchivoComprobante(Part archivoComprobante) {
        this.archivoComprobante = archivoComprobante;
    }

    public List<Pago> getPagos() {
        return pagosFiltrados;
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

    public void setPagoSeleccionado(Pago pagoSeleccionado) {
        this.pagoSeleccionado = pagoSeleccionado;
    }

    public double getTotalPagos() {
        return pagos.stream()
                .filter(p -> "Completado".equals(p.getEstado()))
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    public int getPagosCompletados() {
        return (int) pagos.stream()
                .filter(p -> "Completado".equals(p.getEstado()))
                .count();
    }

    public int getPagosPendientes() {
        return (int) pagos.stream()
                .filter(p -> "Pendiente".equals(p.getEstado()))
                .count();
    }

    public int getPagosCancelados() {
        return (int) pagos.stream()
                .filter(p -> "Cancelado".equals(p.getEstado()))
                .count();
    }
}
