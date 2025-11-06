package com.refaccionaria.proveedoresInternos.beans;

<<<<<<< HEAD
import jakarta.annotation.PostConstruct;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.refaccionaria.proveedoresInternos.models.Inventario;
import jakarta.enterprise.context.SessionScoped;
=======
import com.refaccionaria.proveedoresInternos.models.Inventario;
import com.refaccionaria.proveedoresInternos.models.Producto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
>>>>>>> juan-inventario-pagos
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
<<<<<<< HEAD

=======
import java.util.Optional;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

/**
 * Bean para manejar inventarios: lista, alta, búsqueda y actualización por
 * venta/devolución.
 */
>>>>>>> juan-inventario-pagos
@Named("inventarioBean")
@SessionScoped
public class InventarioBean implements Serializable {

<<<<<<< HEAD
    private List<Inventario> inventarios = new ArrayList<>();

    @PostConstruct
=======
    private static final long serialVersionUID = 1L;

    @Inject
    private CatalogoBean catalogoBean; // para obtener lista de productos y datos

    private List<Inventario> inventarios = new ArrayList<>();
    private List<Inventario> inventariosFiltrados = new ArrayList<>();

    private Inventario inventarioActual = new Inventario();
    private Inventario selectedInventario;

    private String terminoBusqueda = "";
    private String filtroBusqueda = "producto"; // valores posibles: producto, ubicacion, id

    @PostConstruct
    public void init() {
        if (verificarConexionMongoDB()) {
            cargarInventariosDesdeMongo();
            actualizarFiltrados();
        }
    }

    private boolean verificarConexionMongoDB() {
        try {
            MongoDatabase db = LoginBean.getDatabase();
            // Intenta una operación simple para verificar la conexión
            db.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            String error = "Error de conexión a MongoDB: " + e.getMessage();
            System.err.println(error);
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de conexión",
                            "No se pudo conectar a MongoDB. Usando datos de ejemplo."));
            return false;
        }
    }

    // === MongoDB: carga y persistencia ===
    private MongoCollection<Document> getInventarioCollection() {
        try {
            MongoDatabase db = LoginBean.getDatabase();
            return db.getCollection("inventario");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cargarInventariosDesdeMongo() {
        inventarios.clear();
        MongoCollection<Document> coll = getInventarioCollection();
        if (coll == null) {
            // si no hay conexion, usar ejemplos
            cargarInventariosEjemplo();
            return;
        }
        try (MongoCursor<Document> cursor = coll.find().iterator()) {
            boolean any = false;
            while (cursor.hasNext()) {
                any = true;
                Document doc = cursor.next();
                Inventario inv = new Inventario();
                inv.setId(doc.getObjectId("_id").toString());
                inv.setProductoId(doc.getString("codigoProducto"));
                // valores numéricos pueden venir como Integer o Double
                Object stockObj = doc.get("stock");
                inv.setStock(stockObj instanceof Number ? ((Number) stockObj).intValue() : 0);
                inv.setUbicacion(doc.getString("ubicacion"));
                Object minimoObj = doc.get("minimo");
                inv.setMinimo(minimoObj instanceof Number ? ((Number) minimoObj).intValue() : 0);
                Object maxObj = doc.get("maximo");
                inv.setMaximo(maxObj instanceof Number ? ((Number) maxObj).intValue() : 0);
                inv.setFechaActualizacion(doc.getDate("fecha_actualizacion"));
                // La descripción no existe en la BD, la dejamos vacía
                inv.setDescripcion("");
                inventarios.add(inv);
            }
            if (!any) {
                // colección vacía -> usar ejemplos
                cargarInventariosEjemplo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // en caso de error regresar a ejemplos
            cargarInventariosEjemplo();
        }
    }

    // --- Inicializadores / ejemplos ---
>>>>>>> juan-inventario-pagos
    public void cargarInventariosEjemplo() {
        inventarios.clear();

        Inventario inv1 = new Inventario();
        inv1.setId("INV-001");
        inv1.setProductoId("PROD-001");
        inv1.setStock(50);
        inv1.setUbicacion("Almacén Central");
        inv1.setMinimo(10);
        inv1.setMaximo(100);
<<<<<<< HEAD
        inv1.setFechaActualizacion(new java.util.Date());
        inv1.setDescripcion("Aceite para motor 5W-30, sintético, 1L");
=======
        inv1.setFechaActualizacion(new Date());
        inv1.setDescripcion("Inventario inicial producto 1");
>>>>>>> juan-inventario-pagos

        Inventario inv2 = new Inventario();
        inv2.setId("INV-002");
        inv2.setProductoId("PROD-002");
        inv2.setStock(5);
        inv2.setUbicacion("Sucursal Norte");
        inv2.setMinimo(5);
        inv2.setMaximo(50);
<<<<<<< HEAD
        inv2.setFechaActualizacion(new java.util.Date());
        inv2.setDescripcion("Filtro de aire para Nissan Versa 2020");
=======
        inv2.setFechaActualizacion(new Date());
        inv2.setDescripcion("Inventario con stock bajo");
>>>>>>> juan-inventario-pagos

        Inventario inv3 = new Inventario();
        inv3.setId("INV-003");
        inv3.setProductoId("PROD-003");
        inv3.setStock(0);
        inv3.setUbicacion("Almacén Central");
        inv3.setMinimo(2);
        inv3.setMaximo(30);
<<<<<<< HEAD
        inv3.setFechaActualizacion(new java.util.Date());
        inv3.setDescripcion("Bujía NGK estándar");
=======
        inv3.setFechaActualizacion(new Date());
        inv3.setDescripcion("Agotado");
>>>>>>> juan-inventario-pagos

        inventarios.add(inv1);
        inventarios.add(inv2);
        inventarios.add(inv3);
    }

<<<<<<< HEAD
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
=======
    // --- Operaciones CRUD / utilidades ---
    public void agregarInventario() {
        if (inventarioActual == null || inventarioActual.getProductoId() == null
                || inventarioActual.getProductoId().isBlank()) {
            // dejar que la vista valide, o agregar FacesMessage aquí
            return;
        }
        inventarioActual.setId(generarId());
        inventarioActual.setFechaActualizacion(new Date());
        // persistir en MongoDB
        try {
            guardarInventarioEnDB(inventarioActual);
            inventarios.add(inventarioActual);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Inventario creado",
                            "El inventario se creó correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear",
                            "No se pudo crear el inventario. Ver registros."));
            e.printStackTrace();
        }
        inventarioActual = new Inventario();
        actualizarFiltrados();
    }

    public void actualizarInventario(Inventario inv) {
        inv.setFechaActualizacion(new Date());
        // persistir cambios en DB
        try {
            actualizarInventarioEnDB(inv);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Inventario actualizado",
                            "Cambios guardados correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar",
                            "No se pudieron guardar los cambios."));
            e.printStackTrace();
        }
        actualizarFiltrados();
    }

    public void eliminarInventario(String id) {
        // eliminar en DB
        try {
            eliminarInventarioEnDB(id);
            inventarios.removeIf(i -> i.getId().equals(id));
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Inventario eliminado",
                            "Registro eliminado correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar",
                            "No se pudo eliminar el registro."));
            e.printStackTrace();
        }
        actualizarFiltrados();
    }

    // --- Búsqueda / filtros ---
    public void buscar() {
        actualizarFiltrados();
    }

    private void actualizarFiltrados() {
        inventariosFiltrados.clear();
        String term = terminoBusqueda == null ? "" : terminoBusqueda.trim().toLowerCase();
        if (term.isEmpty()) {
            inventariosFiltrados.addAll(inventarios);
            return;
        }
        for (Inventario inv : inventarios) {
            switch (filtroBusqueda) {
                case "ubicacion":
                    if (inv.getUbicacion() != null && inv.getUbicacion().toLowerCase().contains(term)) {
                        inventariosFiltrados.add(inv);
                    }
                    break;
                case "id":
                    if (inv.getId() != null && inv.getId().toLowerCase().contains(term)) {
                        inventariosFiltrados.add(inv);
                    }
                    break;
                case "producto":
                default:
                    // buscar por productoId y también por nombre si CatalogoBean provee productos
                    boolean matched = false;
                    if (inv.getProductoId() != null && inv.getProductoId().toLowerCase().contains(term)) {
                        matched = true;
                    } else {
                        Producto p = buscarProducto(inv.getProductoId());
                        if (p != null && p.getNombre() != null && p.getNombre().toLowerCase().contains(term)) {
                            matched = true;
                        }
                    }
                    if (matched)
                        inventariosFiltrados.add(inv);
            }
        }
    }

    // --- Actualización por venta / devolución ---
    public boolean procesarVenta(String productoId, int cantidad) {
        Optional<Inventario> opt = inventarios.stream().filter(i -> productoId.equals(i.getProductoId())).findFirst();
        if (opt.isPresent()) {
            Inventario inv = opt.get();
            int nuevo = inv.getStock() - cantidad;
            inv.setStock(Math.max(0, nuevo));
            inv.setFechaActualizacion(new Date());
            // persistir cambio
            try {
                actualizarInventarioEnDB(inv);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta procesada",
                                "Stock actualizado correctamente."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el stock."));
                e.printStackTrace();
            }
            actualizarFiltrados();
            return true;
        }
        return false;
    }

    public boolean procesarDevolucion(String productoId, int cantidad) {
        Optional<Inventario> opt = inventarios.stream().filter(i -> productoId.equals(i.getProductoId())).findFirst();
        if (opt.isPresent()) {
            Inventario inv = opt.get();
            inv.setStock(inv.getStock() + cantidad);
            inv.setFechaActualizacion(new Date());
            // persistir cambio
            try {
                actualizarInventarioEnDB(inv);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Devolución procesada",
                                "Stock actualizado correctamente."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el stock."));
                e.printStackTrace();
            }
            actualizarFiltrados();
            return true;
        } else {
            // crear registro si no existe
            Inventario nuevo = new Inventario();
            nuevo.setId(generarId());
            nuevo.setProductoId(productoId);
            nuevo.setStock(cantidad);
            nuevo.setUbicacion("Pendiente asignación");
            nuevo.setFechaActualizacion(new Date());
            // persistir nuevo inventario
            try {
                guardarInventarioEnDB(nuevo);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Devolución creada",
                                "Se creó un nuevo registro de inventario."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el registro."));
                e.printStackTrace();
            }
            inventarios.add(nuevo);
            actualizarFiltrados();
            return true;
        }
    }

    // --- Detalle / navegación ---
    public String verDetalles(String inventarioId) {
        for (Inventario i : inventarios) {
            if (i.getId().equals(inventarioId)) {
                selectedInventario = i;
                return "/inventario/detalle_inventarios.xhtml?faces-redirect=true";
            }
        }
        return null;
    }

    public String modificarStock() {
        if (selectedInventario == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se ha seleccionado ningún inventario para modificar."));
            return null;
        }
        // Redirigir a la página de edición
        return "/inventario/editar_stock.xhtml?faces-redirect=true";
    }

    public String guardarCambiosStock() {
        if (selectedInventario == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se ha seleccionado ningún inventario."));
            return null;
        }

        // Validar que máximo sea mayor que mínimo
        if (selectedInventario.getMaximo() <= selectedInventario.getMinimo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "El stock máximo debe ser mayor que el stock mínimo."));
            return null;
        }

        try {
            // Actualizar en MongoDB
            actualizarInventarioEnDB(selectedInventario);

            // Mantener mensajes tras redirect usando Flash
            FacesContext fc = FacesContext.getCurrentInstance();
            try {
                fc.getExternalContext().getFlash().setKeepMessages(true);
            } catch (Exception ignore) {
                // En algunas configuraciones el Flash puede no estar disponible; ignorar
            }
            fc.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado",
                            "Los niveles de stock se actualizaron correctamente."));

            // Redirigir a la página de detalles
            return "/inventario/detalle_inventarios.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudieron guardar los cambios: " + e.getMessage()));
            return null;
        }
    }

    // --- Helpers ---
    private String generarId() {
        return "INV-" + System.currentTimeMillis() % 100000 + "-" + (int) (Math.random() * 900);
    }

    private Producto buscarProducto(String productoId) {
        if (catalogoBean == null)
            return null;
        if (catalogoBean.getProductos() == null)
            return null;
        for (Producto p : catalogoBean.getProductos()) {
            if (p.getId() != null && p.getId().equals(productoId))
                return p;
        }
        return null;
    }

    /**
     * Helper para obtener el nombre del producto. Si no se encuentra el producto
     * devuelve el id (código).
     */
    public String nombreProducto(String productoId) {
        Producto p = buscarProducto(productoId);
        return (p != null && p.getNombre() != null && !p.getNombre().isBlank()) ? p.getNombre() : productoId;
    }

    /**
     * Helper para mostrar "Nombre (Código)" en vistas. Si no existe el nombre,
     * devuelve el código.
     */
    public String nombreProductoDisplay(String productoId) {
        Producto p = buscarProducto(productoId);
        if (p != null && p.getNombre() != null && !p.getNombre().isBlank()) {
            return p.getNombre() + " (" + productoId + ")";
        }
        return productoId;
    }

    /**
     * Retorna la clase CSS para el estado según reglas de negocio:
     * - stock == 0 -> out-stock
     * - stock <= minimo -> min-stock
     * - en otro caso -> in-stock
     */
    public String estadoClass(Inventario inv) {
        if (inv == null)
            return "";
        int stock = inv.getStock();
        int minimo = inv.getMinimo();
        if (stock <= 0)
            return "out-stock";
        if (stock <= minimo)
            return "min-stock";
        return "in-stock";
    }

    /**
     * Etiqueta legible del estado para la vista.
     */
    public String estadoLabel(Inventario inv) {
        if (inv == null)
            return "";
        int stock = inv.getStock();
        int minimo = inv.getMinimo();
        if (stock <= 0)
            return "Sin Stock";
        if (stock <= minimo)
            return "Stock Mínimo";
        return "En Stock";
    }

    // === Persistencia en MongoDB: helpers ===
    private void guardarInventarioEnDB(Inventario inv) {
        MongoCollection<Document> coll = getInventarioCollection();
        if (coll == null)
            return;
        try {
            Document doc = new Document("codigoProducto", inv.getProductoId())
                    .append("stock", inv.getStock())
                    .append("ubicacion", inv.getUbicacion())
                    .append("minimo", inv.getMinimo())
                    .append("maximo", inv.getMaximo())
                    .append("fecha_actualizacion", inv.getFechaActualizacion());
            coll.insertOne(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarInventarioEnDB(Inventario inv) {
        MongoCollection<Document> coll = getInventarioCollection();
        if (coll == null)
            return;
        try {
            Document doc = new Document("codigoProducto", inv.getProductoId())
                    .append("stock", inv.getStock())
                    .append("ubicacion", inv.getUbicacion())
                    .append("minimo", inv.getMinimo())
                    .append("maximo", inv.getMaximo())
                    .append("fecha_actualizacion", inv.getFechaActualizacion());
            coll.updateOne(new Document("_id", new org.bson.types.ObjectId(inv.getId())), new Document("$set", doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarInventarioEnDB(String id) {
        MongoCollection<Document> coll = getInventarioCollection();
        if (coll == null)
            return;
        try {
            coll.deleteOne(new Document("_id", new org.bson.types.ObjectId(id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Productos disponibles para inventario ---
    public List<Producto> getProductosDisponibles() {
        MongoCollection<Document> productos = LoginBean.getDatabase().getCollection("productos");
        MongoCollection<Document> inventario = getInventarioCollection();

        // Obtenemos los códigos de productos que ya tienen inventario
        List<String> productosConInventario = new ArrayList<>();
        try (MongoCursor<Document> cursor = inventario.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                productosConInventario.add(doc.getString("codigoProducto"));
            }
        }

        // Obtenemos los productos que no están en inventario
        List<Producto> disponibles = new ArrayList<>();
        try (MongoCursor<Document> cursor = productos.find(
                new Document("codigoProducto",
                        new Document("$nin", productosConInventario)))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Producto p = new Producto();
                p.setId(doc.getObjectId("_id").toString());
                p.setCodigoProducto(doc.getString("codigoProducto"));
                p.setNombre(doc.getString("nombre"));
                Number precioCompraNum = doc.get("precioCompra", Number.class);
                if (precioCompraNum != null) {
                    p.setPrecioCompra(precioCompraNum.doubleValue());
                }
                Number precioVentaNum = doc.get("precioVenta", Number.class);
                if (precioVentaNum != null) {
                    p.setPrecioVenta(precioVentaNum.doubleValue());
                }
                disponibles.add(p);
            }
        }

        if (disponibles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "No hay productos disponibles",
                            "Todos los productos actuales ya tienen un registro de inventario asociado. " +
                                    "Para agregar nuevo inventario, primero debe agregar nuevos productos al catálogo."));
        }

        return disponibles;
    }

    // --- Getters / Setters ---
    public List<Inventario> getInventarios() {
        return inventarios;
    }

    public List<Inventario> getInventariosFiltrados() {
        return inventariosFiltrados;
    }

    public Inventario getInventarioActual() {
        return inventarioActual;
    }

    public void setInventarioActual(Inventario inventarioActual) {
        this.inventarioActual = inventarioActual;
    }

    public Inventario getSelectedInventario() {
        return selectedInventario;
    }

    public void setSelectedInventario(Inventario selectedInventario) {
        this.selectedInventario = selectedInventario;
    }
>>>>>>> juan-inventario-pagos

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

<<<<<<< HEAD
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
=======
    // Estadísticas simples para dashboard
    public int getUnidadesTotales() {
        int total = 0;
        for (Inventario i : inventarios)
            total += i.getStock();
        return total;
    }

    public int getProductosSinStock() {
        int c = 0;
        for (Inventario i : inventarios)
            if (i.getStock() <= 0)
                c++;
        return c;
    }

    public int getProductosStockMinimo() {
        int c = 0;
        for (Inventario i : inventarios)
            if (i.getStock() <= i.getMinimo())
                c++;
        return c;
    }

    // --- Método para recargar inventario al cargar la página ---
    public void onPreRenderView() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            // Recargar inventarios desde MongoDB para reflejar cambios recientes (ej.
            // devoluciones)
            if (verificarConexionMongoDB()) {
                cargarInventariosDesdeMongo();
                actualizarFiltrados();
>>>>>>> juan-inventario-pagos
            }
        }
    }

<<<<<<< HEAD
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
=======
    // --- Getters auxiliares para la vista de detalle ---
    public String getDetalleId() {
        return selectedInventario != null ? selectedInventario.getId() : "";
    }

    public String getDetalleUbicacion() {
        return selectedInventario != null ? selectedInventario.getUbicacion() : "";
    }

    public String getDetalleDescripcion() {
        return selectedInventario != null ? selectedInventario.getDescripcion() : "";
    }

    public int getDetalleMinimo() {
        return selectedInventario != null ? selectedInventario.getMinimo() : 0;
    }

    public int getDetalleMaximo() {
        return selectedInventario != null ? selectedInventario.getMaximo() : 0;
    }

    public String getDetalleNombre() {
        if (selectedInventario == null)
            return "";
        Producto p = buscarProducto(selectedInventario.getProductoId());
        return p != null ? p.getNombre() : selectedInventario.getProductoId();
    }

    public String getDetalleImagen() {
        if (selectedInventario == null)
            return null;
        Producto p = buscarProducto(selectedInventario.getProductoId());
        if (p != null && p.getImagenRuta() != null)
            return p.getImagenRuta();
        return null;
>>>>>>> juan-inventario-pagos
    }
}
