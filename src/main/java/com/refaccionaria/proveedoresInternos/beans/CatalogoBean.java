package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.models.Producto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class CatalogoBean implements Serializable {

    private String terminoBusqueda;
    private List<Producto> productos;
    private List<Producto> productosFiltrados;

    public CatalogoBean() {
        cargarProductos();
    }

    private void cargarProductos() {
        productos = new ArrayList<>();

        // Motor
        productos.add(crearProducto(
                "ENG-001", "Bomba de Aceite Bosch", "Bosch", "Motor",
                2499.00, crearCaracteristicas("Acero", "Alta presión", "Compatible con motores 1.6-2.0L"),
                "fas fa-cogs"
        ));

        productos.add(crearProducto(
                "ENG-002", "Juego de Pistones Mahle", "Mahle", "Motor",
                3999.00, crearCaracteristicas("Aluminio", "4 pistones", "Kit completo"),
                "fas fa-cogs"
        ));

// Transmisión
        productos.add(crearProducto(
                "TRS-001", "Embrague Luk", "Luk", "Transmisión",
                3499.00, crearCaracteristicas("Kit completo", "Diámetro 228mm", "Alta durabilidad"),
                "fas fa-cog"
        ));

        productos.add(crearProducto(
                "TRS-002", "Palier Derecho SKF", "SKF", "Transmisión",
                1999.00, crearCaracteristicas("Acero", "Compatible con Toyota Corolla 2018-2023"),
                "fas fa-cog"
        ));

// Frenos
        productos.add(crearProducto(
                "BRK-001", "Pastillas de Freno Brembo", "Brembo", "Frenos",
                1599.00, crearCaracteristicas("Cerámica", "Delanteras", "Compatible con Honda Civic"),
                "fas fa-stop-circle"
        ));

        productos.add(crearProducto(
                "BRK-002", "Disco de Freno ATE", "ATE", "Frenos",
                2499.00, crearCaracteristicas("Ventilado", "Delantero", "Acero de alta resistencia"),
                "fas fa-stop-circle"
        ));

// Suspensión
        productos.add(crearProducto(
                "SUS-001", "Amortiguador KYB", "KYB", "Suspensión",
                1899.00, crearCaracteristicas("Delantero", "Gas", "Compatible con Nissan Altima"),
                "fas fa-arrows-alt-v"
        ));

        productos.add(crearProducto(
                "SUS-002", "Resorte Eibach", "Eibach", "Suspensión",
                1299.00, crearCaracteristicas("De acero", "Juego completo", "Sport"),
                "fas fa-arrows-alt-v"
        ));

// Sistema Eléctrico
        productos.add(crearProducto(
                "ELE-001", "Batería Bosch 12V 60Ah", "Bosch", "Sistema Eléctrico",
                2199.00, crearCaracteristicas("12V", "60Ah", "Garantía 12 meses"),
                "fas fa-bolt"
        ));

        productos.add(crearProducto(
                "ELE-002", "Alternador Denso", "Denso", "Sistema Eléctrico",
                3999.00, crearCaracteristicas("12V", "120A", "Compatible con Toyota y Honda"),
                "fas fa-bolt"
        ));

// Refrigeración
        productos.add(crearProducto(
                "COOL-001", "Radiador Valeo", "Valeo", "Refrigeración",
                2799.00, crearCaracteristicas("Aluminio", "Para motor 1.6-2.0L", "Alta eficiencia"),
                "fas fa-tint"
        ));

        productos.add(crearProducto(
                "COOL-002", "Ventilador eléctrico Hella", "Hella", "Refrigeración",
                1499.00, crearCaracteristicas("12V", "Electrónico", "Compatible con VW Golf"),
                "fas fa-tint"
        ));

        productosFiltrados = new ArrayList<>(productos);
    }

    private Producto crearProducto(String codigo, String nombre, String marca,
            String categoria, double precio,
            Map<String, String> caracteristicas, String icono) {
        Producto producto = new Producto();
        producto.setId(generarId());
        producto.setCodigoProducto(codigo);
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setCaracteristicas(caracteristicas);
        producto.setFechaCreacion(new Date());
        producto.setUltimaActualizacion(new Date());

        // Agregar icono como caracteristica adicional para el frontend
        if (producto.getCaracteristicas() == null) {
            producto.setCaracteristicas(new HashMap<>());
        }
        producto.getCaracteristicas().put("icono", icono);

        return producto;
    }

    private Map<String, String> crearCaracteristicas(String... caracteristicas) {
        Map<String, String> mapa = new HashMap<>();
        if (caracteristicas.length >= 1) {
            mapa.put("especificacion1", caracteristicas[0]);
        }
        if (caracteristicas.length >= 2) {
            mapa.put("especificacion2", caracteristicas[1]);
        }
        if (caracteristicas.length >= 3) {
            mapa.put("especificacion3", caracteristicas[2]);
        }
        return mapa;
    }

    private String generarId() {
        return "PROD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }

    public void buscarProductos() {
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            productosFiltrados = new ArrayList<>(productos);
        } else {
            productosFiltrados = new ArrayList<>();
            for (Producto p : productos) {
                if (p.getNombre().toLowerCase().contains(terminoBusqueda.toLowerCase())
                        || p.getMarca().toLowerCase().contains(terminoBusqueda.toLowerCase())
                        || p.getCategoria().toLowerCase().contains(terminoBusqueda.toLowerCase())) {
                    productosFiltrados.add(p);
                }
            }
        }
    }

    public void filtrarPorCategoria(String categoria) {
        productosFiltrados = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                productosFiltrados.add(p);
            }
        }
    }

    public void agregarAlCarrito(Producto producto) {
        System.out.println("Producto agregado al carrito: " + producto.getNombre());
        System.out.println("Codigo: " + producto.getCodigoProducto());
        System.out.println("Precio: $" + producto.getPrecio());
        // Aqui puedes implementar la logica real del carrito de compras
    }

    public void verDetalles(Producto producto) {
        System.out.println("=== DETALLES DEL PRODUCTO ===");
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Marca: " + producto.getMarca());
        System.out.println("Categoria: " + producto.getCategoria());
        System.out.println("Precio: $" + producto.getPrecio());
        System.out.println("Codigo: " + producto.getCodigoProducto());
        System.out.println("Caracteristicas: " + producto.getCaracteristicas());
        // Aqui puedes navegar a una pagina de detalles del producto
    }

    // Metodos auxiliares para el frontend
    public String getIcono(Producto producto) {
        if (producto.getCaracteristicas() == null) {
            return "fas fa-box";
        }
        String icono = producto.getCaracteristicas().get("icono");
        return icono != null ? icono : "fas fa-box";
    }

    public String getEtiqueta(Producto producto) {
        // Logica para determinar etiquetas (nuevo, oferta, etc.)
        if (producto.getPrecio() > 20000) {
            return "Premium";
        }
        if (producto.getPrecio() < 1000) {
            return "Oferta";
        }
        if (producto.getMarca().equals("Intel") || producto.getMarca().equals("AMD")) {
            return "Nuevo";
        }
        return "";
    }

    public int getStockSimulado(Producto producto) {
        // Stock simulado basado en el codigo del producto
        String codigo = producto.getCodigoProducto();
        if (codigo == null) {
            return 0;
        }
        if (codigo.startsWith("PROC")) {
            return 15;
        }
        if (codigo.startsWith("RAM")) {
            return 8;
        }
        if (codigo.startsWith("SSD")) {
            return 25;
        }
        if (codigo.startsWith("HDD")) {
            return 12;
        }
        if (codigo.startsWith("GPU")) {
            return 3;
        }
        if (codigo.startsWith("COOL")) {
            return 18;
        }
        return 10;
    }

    public String getClaseStock(Producto producto) {
        int stock = getStockSimulado(producto);
        if (stock > 10) {
            return "stock-high";
        }
        if (stock > 0) {
            return "stock-low";
        }
        return "stock-none";
    }

    public String getTextoStock(Producto producto) {
        int stock = getStockSimulado(producto);
        if (stock > 10) {
            return "En stock";
        }
        if (stock > 0) {
            return "Ultimas unidades";
        }
        return "Agotado";
    }

    // Getters y Setters
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
}
