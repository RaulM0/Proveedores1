package com.refaccionaria.proveedoresInternos.beans;

import jakarta.annotation.PostConstruct;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.refaccionaria.proveedoresInternos.models.Inventario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("inventarioBean")
@SessionScoped
public class InventarioBean implements Serializable {

    private List<Inventario> inventarios = new ArrayList<>();

    @PostConstruct
    public void cargarInventariosEjemplo() {
        inventarios.clear();

        Inventario inv1 = new Inventario();
        inv1.setId("INV-001");
        inv1.setProductoId("PROD-001");
        inv1.setStock(50);
        inv1.setUbicacion("Almacén Central");
        inv1.setMinimo(10);
        inv1.setMaximo(100);
        inv1.setFechaActualizacion(new java.util.Date());
        inv1.setDescripcion("Aceite para motor 5W-30, sintético, 1L");

        Inventario inv2 = new Inventario();
        inv2.setId("INV-002");
        inv2.setProductoId("PROD-002");
        inv2.setStock(5);
        inv2.setUbicacion("Sucursal Norte");
        inv2.setMinimo(5);
        inv2.setMaximo(50);
        inv2.setFechaActualizacion(new java.util.Date());
        inv2.setDescripcion("Filtro de aire para Nissan Versa 2020");

        Inventario inv3 = new Inventario();
        inv3.setId("INV-003");
        inv3.setProductoId("PROD-003");
        inv3.setStock(0);
        inv3.setUbicacion("Almacén Central");
        inv3.setMinimo(2);
        inv3.setMaximo(30);
        inv3.setFechaActualizacion(new java.util.Date());
        inv3.setDescripcion("Bujía NGK estándar");

        inventarios.add(inv1);
        inventarios.add(inv2);
        inventarios.add(inv3);
    }

    private Inventario inventarioActual = new Inventario();

    // Propiedades para búsqueda y filtro
    private String terminoBusqueda;
    private String filtroBusqueda = "productoId";
    private List<Inventario> inventariosOriginal = new ArrayList<>();

    // Propiedades para detalle de inventario
    private String detalleId;
    private String detalleNombre;
    private String detalleUbicacion;
    private String detalleDescripcion;
    private int detalleMinimo;
    private int detalleMaximo;
    private String detalleImagen;
    private boolean historialVisible = false;
    private String terminoBusquedaHistorial;
    private List<MovimientoInventario> historialMovimientos = new ArrayList<>();

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public String getFiltroBusqueda() {
        return filtroBusqueda;
    }

    public void setFiltroBusqueda(String filtroBusqueda) {
        this.filtroBusqueda = filtroBusqueda;
    }

    // Getters y setters para detalle
    public String getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(String detalleId) {
        this.detalleId = detalleId;
    }

    public String getDetalleNombre() {
        return detalleNombre;
    }

    public void setDetalleNombre(String detalleNombre) {
        this.detalleNombre = detalleNombre;
    }

    public String getDetalleUbicacion() {
        return detalleUbicacion;
    }

    public void setDetalleUbicacion(String detalleUbicacion) {
        this.detalleUbicacion = detalleUbicacion;
    }

    public String getDetalleDescripcion() {
        return detalleDescripcion;
    }

    public void setDetalleDescripcion(String detalleDescripcion) {
        this.detalleDescripcion = detalleDescripcion;
    }

    public int getDetalleMinimo() {
        return detalleMinimo;
    }

    public void setDetalleMinimo(int detalleMinimo) {
        this.detalleMinimo = detalleMinimo;
    }

    public int getDetalleMaximo() {
        return detalleMaximo;
    }

    public void setDetalleMaximo(int detalleMaximo) {
        this.detalleMaximo = detalleMaximo;
    }

    public String getDetalleImagen() {
        return detalleImagen;
    }

    public void setDetalleImagen(String detalleImagen) {
        this.detalleImagen = detalleImagen;
    }

    public boolean isHistorialVisible() {
        return historialVisible;
    }

    public void setHistorialVisible(boolean historialVisible) {
        this.historialVisible = historialVisible;
    }

    public String getTerminoBusquedaHistorial() {
        return terminoBusquedaHistorial;
    }

    public void setTerminoBusquedaHistorial(String terminoBusquedaHistorial) {
        this.terminoBusquedaHistorial = terminoBusquedaHistorial;
    }

    public List<MovimientoInventario> getHistorialMovimientos() {
        return historialMovimientos;
    }

    public void setHistorialMovimientos(List<MovimientoInventario> historialMovimientos) {
        this.historialMovimientos = historialMovimientos;
    }

    // Método de búsqueda con filtro
    public void buscarInventario() {
        if (inventariosOriginal.isEmpty()) {
            inventariosOriginal.addAll(inventarios);
        }
        if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
            List<Inventario> filtrados = new ArrayList<>();
            for (Inventario inv : inventariosOriginal) {
                String valor = "";
                switch (filtroBusqueda) {
                    case "productoId":
                        valor = inv.getProductoId();
                        break;
                    case "ubicacion":
                        valor = inv.getUbicacion();
                        break;
                    case "id":
                        valor = inv.getId();
                        break;
                }
                if (valor != null && valor.toLowerCase().contains(terminoBusqueda.toLowerCase())) {
                    filtrados.add(inv);
                }
            }
            inventarios = filtrados;
        } else {
            inventarios = new ArrayList<>(inventariosOriginal);
        }
    }

    // Para dashboard: métodos compatibles con EL
    public int getUnidadesTotales() {
        int total = 0;
        for (Inventario inv : inventarios) {
            total += inv.getStock();
        }
        return total;
    }

    public int getProductosStockMinimo() {
        int count = 0;
        for (Inventario inv : inventarios) {
            if (inv.getStock() <= inv.getMinimo()) {
                count++;
            }
        }
        return count;
    }

    public int getProductosSinStock() {
        int count = 0;
        for (Inventario inv : inventarios) {
            if (inv.getStock() == 0) {
                count++;
            }
        }
        return count;
    }

    // Agrega un nuevo registro de inventario
    public void agregarInventario() {
        inventarioActual.setId(generarId());
        inventarioActual.setFechaActualizacion(new Date());
        inventarios.add(inventarioActual);
        inventarioActual = new Inventario(); // Limpia el formulario
    }

    // Actualiza el stock de un producto (manual)
    public void actualizarStock(String productoId, int nuevoStock) {
        for (Inventario inv : inventarios) {
            if (inv.getProductoId().equals(productoId)) {
                inv.setStock(nuevoStock);
                inv.setFechaActualizacion(new Date());
                break;
            }
        }
    }

    // Actualiza inventario al realizar una venta
    public void procesarVenta(List<com.refaccionaria.proveedoresInternos.models.DetalleVenta> detallesVenta) {
        for (com.refaccionaria.proveedoresInternos.models.DetalleVenta detalle : detallesVenta) {
            for (Inventario inv : inventarios) {
                if (inv.getProductoId().equals(detalle.getProductoId())) {
                    int nuevoStock = inv.getStock() - detalle.getCantidad();
                    inv.setStock(Math.max(nuevoStock, 0));
                    inv.setFechaActualizacion(new Date());
                }
            }
        }
    }

    // Actualiza inventario al procesar una devolución
    public void procesarDevolucion(com.refaccionaria.proveedoresInternos.models.Devolucion devolucion) {
        for (Inventario inv : inventarios) {
            if (inv.getProductoId().equals(devolucion.getProductoId())) {
                inv.setStock(inv.getStock() + devolucion.getCantidad());
                inv.setFechaActualizacion(new Date());
            }
        }
    }

    // Consulta el stock de un producto
    public int consultarStock(String productoId) {
        for (Inventario inv : inventarios) {
            if (inv.getProductoId().equals(productoId)) {
                return inv.getStock();
            }
        }
        return 0;
    }

    // Lista todo el inventario
    public List<Inventario> getInventarios() {
        return inventarios;
    }

    public Inventario getInventarioActual() {
        return inventarioActual;
    }

    public void setInventarioActual(Inventario inventarioActual) {
        this.inventarioActual = inventarioActual;
    }

    // Genera un ID simple para el inventario
    private String generarId() {
        return "INV-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }

    // Acción para mostrar detalles
    public String verDetalle(String id) {
        Inventario inv = inventarios.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
        if (inv != null) {
            detalleId = inv.getId();
            detalleNombre = "Producto " + inv.getProductoId();
            detalleUbicacion = inv.getUbicacion();
            detalleDescripcion = inv.getDescripcion() != null ? inv.getDescripcion() : "Sin descripción";
            detalleMinimo = inv.getMinimo();
            detalleMaximo = inv.getMaximo();
            detalleImagen = "/resources/img/producto_default.png";
            historialVisible = false;
        }
        return "detalle_inventarios.xhtml?faces-redirect=true";
    }

    // Acción para mostrar historial
    public void mostrarHistorial() {
        historialVisible = true;
        cargarHistorialEjemplo();
    }

    // Acción para modificar stock (solo ejemplo)
    public void modificarStock() {
        // Lógica para modificar stock mínimo y máximo
    }

    // Acción para buscar en historial
    public void buscarHistorial() {
        // Filtrar historialMovimientos según terminoBusquedaHistorial
    }

    // Acción para crear reporte
    public void crearReporte() {
        // Lógica para crear reporte
    }

    // Ejemplo de historial
    private void cargarHistorialEjemplo() {
        historialMovimientos.clear();
        historialMovimientos.add(new MovimientoInventario("Producto A", "Venta", 10, 20, "2025-10-04"));
        historialMovimientos.add(new MovimientoInventario("Producto A", "Devolución", 20, 10, "2025-10-03"));
    }

    // Clase interna para movimientos
    public static class MovimientoInventario {
        private String producto;
        private String motivo;
        private int stockActual;
        private int stockAnterior;
        private String fecha;

        public MovimientoInventario(String producto, String motivo, int stockActual, int stockAnterior, String fecha) {
            this.producto = producto;
            this.motivo = motivo;
            this.stockActual = stockActual;
            this.stockAnterior = stockAnterior;
            this.fecha = fecha;
        }

        public String getProducto() {
            return producto;
        }

        public String getMotivo() {
            return motivo;
        }

        public int getStockActual() {
            return stockActual;
        }

        public int getStockAnterior() {
            return stockAnterior;
        }

        public String getFecha() {
            return fecha;
        }
    }
}
