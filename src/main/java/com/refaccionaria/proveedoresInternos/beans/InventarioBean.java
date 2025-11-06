package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Inventario;
import com.refaccionaria.proveedoresInternos.models.Producto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Optional;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

/**
 * Bean para manejar
 * inventarios: lista, alta, búsqueda y actualización por
 * venta/devolución.
 */
@Named("inventarioBean")
@SessionScoped
public class InventarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CatalogoBean catalogoBean; // para obtener lista de productos y datos

    private List<Inventario> inventarios = new ArrayList<>();
    private List<Inventario> inventariosFiltrados = new ArrayList<>();

    rivate Inventario inventarioActual=new Inventario();
    private Inventario selectedInventario;

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
    public void cargarInventariosEjemplo() {
        inventarios.clear();

        Inventario inv1 = new Inventario();
        inv1.setId("INV-001");
        inv1.setProductoId("PROD-001");
        inv1.setStock(50);
        inv1.setUbicacion("Almacén Central");
        inv1.setMinimo(10);
        inv1.setMaximo(100);
        inv1.setFechaActualizacion(new Date());
        inv1.setDescripcion("Inventario inicial producto 1");

        Inventario inv2 = new Inventario();
        inv2.setId("INV-002");
        inv2.setProductoId("PROD-002");
        inv2.setStock(5);
        inv2.setUbicacion("Sucursal Norte");
        inv2.setMinimo(5);
        inv2.setMaximo(50);
        inv2.setFechaActualizacion(new Date());
        inv2.setDescripcion("Inventario con stock bajo");

        Inventario inv3 = new Inventario();
        inv3.setId("INV-003");
        inv3.setProductoId("PROD-003");
        inv3.setStock(0);
        inv3.setUbicacion("Almacén Central");
        inv3.setMinimo(2);
        inv3.setMaximo(30);
        inv3.setFechaActualizacion(new Date());
        inv3.setDescripcion("Agotado");

        inventarios.add(inv1);
        inventarios.add(inv2);
        inventarios.add(inv3);
    }

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
            }
        }
    }

    // --- Getters auxiliares para la vista de detalle ---
    public String getDetalleId() {
    eturn selectedInventario != null ? selectedInventario.getId() : "";
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
    }
}
