package com.refaccionaria.proveedoresInternos.beans;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.refaccionaria.proveedoresInternos.models.Producto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class CatalogoBean implements Serializable {

    private String terminoBusqueda;
    private List<Producto> productos;
    private List<Producto> productosFiltrados;

    // === Conexión a MongoDB ===
    private MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        return mongoClient.getDatabase("refaccionaria");
    }

    // === Inicializar datos al cargar el bean ===
    @PostConstruct
    public void init() {
        cargarProductosDesdeMongo();
    }

    // === Cargar todos los productos desde Mongo ===
    private void cargarProductosDesdeMongo() {
        productos = new ArrayList<>();
        try {
            MongoCollection<Document> collection = getDatabase().getCollection("productos");
            MongoCursor<Document> cursor = collection.find().iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Producto p = new Producto();

                p.setId(doc.getObjectId("_id").toString());
                p.setCodigoProducto(doc.getString("codigoProducto"));
                p.setNombre(doc.getString("nombre"));
                p.setMarca(doc.getString("marca"));
                p.setCategoria(doc.getString("categoria"));
                p.setPrecioVenta(doc.getDouble("precioVenta"));
                p.setPrecioCompra(doc.getDouble("precioCompra"));
                p.setCaracteristicas(doc.getString("caracteristicas"));
                p.setEstatus(doc.getString("estatus"));
                p.setFechaCreacion(doc.getString("fechaCreacion"));
                p.setFechaActualizacion(doc.getString("fechaActualizacion"));
                p.setImagenRuta(doc.getString("imagenRuta"));

                productos.add(p);
            }
            cursor.close();
            productosFiltrados = new ArrayList<>(productos);

        } catch (Exception e) {
            e.printStackTrace();
            productos = new ArrayList<>();
            productosFiltrados = new ArrayList<>();
        }
    }

    // === Búsqueda ===
    public void buscarProductos() {
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            productosFiltrados = new ArrayList<>(productos);
        } else {
            productosFiltrados = new ArrayList<>();
            for (Producto p : productos) {
                if (p.getNombre().toLowerCase().contains(terminoBusqueda.toLowerCase())
                        || p.getMarca().toLowerCase().contains(terminoBusqueda.toLowerCase())
                        || p.getCategoria().toLowerCase().contains(terminoBusqueda.toLowerCase())
                        || p.getCodigoProducto().toLowerCase().contains(terminoBusqueda.toLowerCase())) {
                    productosFiltrados.add(p);
                }
            }
        }
    }

    // === Filtrar por categoría ===
    public void filtrarPorCategoria(String categoria) {
        productosFiltrados = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)) {
                productosFiltrados.add(p);
            }
        }
    }

    // === Etiqueta visual según estatus ===
    public String getEtiqueta(Producto producto) {
        if (producto.getEstatus() != null && producto.getEstatus().equalsIgnoreCase("Inactivo")) {
            return "Inactivo";
        }
        return "Activo";
    }

    // === Clase CSS para stock/estatus ===
    public String getClaseStock(Producto producto) {
        if (producto == null || producto.getEstatus() == null) {
            return "stock-desconocido";
        }

        if (producto.getEstatus().equalsIgnoreCase("Activo")) {
            return "stock-alto"; // CSS: verde o disponible
        } else {
            return "stock-bajo"; // CSS: rojo o agotado
        }
    }

    // === Texto de stock/estatus ===
    public String getTextoStock(Producto producto) {
        if (producto == null || producto.getEstatus() == null) {
            return "Sin información";
        }

        if (producto.getEstatus().equalsIgnoreCase("Activo")) {
            return "Disponible";
        } else {
            return "Agotado";
        }
    }

    // === Icono según categoría ===
    public String getIcono(Producto producto) {
        if (producto.getCategoria() == null) {
            return "fas fa-box";
        }
        switch (producto.getCategoria().toLowerCase()) {
            case "motor":
                return "fas fa-cog";
            case "transmision":
                return "fas fa-exchange-alt";
            case "sistema de frenos":
                return "fas fa-stop-circle";
            case "suspension y direccion":
                return "fas fa-car-side";
            case "escape y emisiones":
                return "fas fa-wind";
            default:
                return "fas fa-box";
        }
    }

    // === Imagen URL (usa tu servlet) ===
    public String getImagenUrl(Producto producto) {
        if (producto.getImagenRuta() != null && !producto.getImagenRuta().isEmpty()) {
            // Usa el servlet para servir las imágenes
            return "/imagenes/" + producto.getImagenRuta();
        } else {
            return "/imagenes/default.png";
        }
    }

    // === Acciones del catálogo ===
    public void agregarAlCarrito(Producto producto) {
        // Implementación futura
        System.out.println("🛒 Agregado al carrito: " + producto.getNombre());
    }

    public void verDetalles(Producto producto) {
        // Implementación futura
        System.out.println("🔍 Ver detalles de: " + producto.getNombre());
    }

    // === Getters & Setters ===
    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<Producto> getProductosFiltrados() {
        return productosFiltrados;
    }

    public boolean isProductoActivo(Producto producto) {
        return producto.getEstatus() != null && producto.getEstatus().equalsIgnoreCase("Activo");
    }

    public String irACatalogo() {
        cargarProductosDesdeMongo();
        return "catalogo?faces-redirect=true";
    }

}
