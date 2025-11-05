package com.refaccionaria.proveedoresInternos.beans;

import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

@Named("productoBean")
@ViewScoped
public class ProductoBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProductoBean.class.getName());

    // 📁 RUTA FUERA DEL PROYECTO (multiplataforma)
    private static final String UPLOAD_DIR = System.getProperty("user.home") + 
            File.separator + "uploads" + 
            File.separator + "refaccionaria" + 
            File.separator + "productos";

    // --- Conexión a MongoDB ---
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static MongoClient mongoClient;

    private static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DB_NAME);
    }

    // --- Propiedades del Formulario ---
    private String codigoBusqueda;
    private String codigoProducto;
    private String nombre;
    private String marca;
    private String categoria;
    private double precioCompra;
    private double precioVenta;
    private String caracteristicas;
    private String estatus;
    private String fechaCreacion;
    private String fechaActualizacion;
    private Part imagen;
    private String imagenRuta; // Solo el nombre del archivo

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==============================================================  
    // CONSTRUCTOR - Crear carpeta al iniciar
    // ==============================================================
    public ProductoBean() {
        crearDirectorioSiNoExiste();
        LOGGER.info("📁 Directorio de imágenes: " + UPLOAD_DIR);
    }

    private void crearDirectorioSiNoExiste() {
        try {
            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                LOGGER.info("✅ Directorio creado: " + UPLOAD_DIR);
            }
        } catch (IOException e) {
            LOGGER.severe("❌ Error al crear directorio: " + e.getMessage());
        }
    }

    // ==============================================================  
    // MÉTODO: BUSCAR PRODUCTO  
    // ==============================================================
    public void buscarProducto() {
        try {
            MongoDatabase db = getDatabase();
            MongoCollection<Document> productos = db.getCollection("productos");

            Document producto = productos.find(Filters.eq("codigoProducto", codigoBusqueda)).first();

            if (producto != null) {
                codigoProducto = producto.getString("codigoProducto");
                nombre = producto.getString("nombre");
                marca = producto.getString("marca");
                categoria = producto.getString("categoria");
                precioCompra = producto.getDouble("precioCompra");
                precioVenta = producto.getDouble("precioVenta");
                caracteristicas = producto.getString("caracteristicas");
                estatus = producto.getString("estatus");
                fechaCreacion = producto.getString("fechaCreacion");
                fechaActualizacion = producto.getString("fechaActualizacion");
                imagenRuta = producto.getString("imagenRuta");

                LOGGER.info("✅ Producto encontrado: " + codigoProducto + " | Imagen: " + imagenRuta);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto encontrado correctamente");
            } else {
                limpiarCampos();
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró ningún producto con ese código");
                LOGGER.warning("⚠ No se encontró producto con código: " + codigoBusqueda);
            }

        } catch (Exception e) {
            LOGGER.severe("❌ Error al buscar producto: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo buscar el producto");
        }
    }

    // ==============================================================  
    // MÉTODO: EDITAR PRODUCTO  
    // ==============================================================
    public String editarProducto() {
        try {
            if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay un producto cargado para editar");
                return null;
            }

            MongoDatabase db = getDatabase();
            MongoCollection<Document> productos = db.getCollection("productos");

            Document existente = productos.find(Filters.eq("codigoProducto", codigoProducto)).first();

            if (existente != null) {
                String nuevaImagenRuta = imagenRuta; // Conservar la actual

                // Si se subió una nueva imagen
                if (imagen != null && imagen.getSize() > 0) {
                    // Eliminar imagen anterior si existe
                    String imagenAnterior = existente.getString("imagenRuta");
                    if (imagenAnterior != null && !imagenAnterior.equals("default.png")) {
                        eliminarImagen(imagenAnterior);
                    }
                    
                    // Guardar la nueva
                    nuevaImagenRuta = guardarImagenFueraDelProyecto(imagen);
                }

                productos.updateOne(
                        Filters.eq("codigoProducto", codigoProducto),
                        Updates.combine(
                                Updates.set("nombre", nombre),
                                Updates.set("marca", marca),
                                Updates.set("categoria", categoria),
                                Updates.set("precioCompra", precioCompra),
                                Updates.set("precioVenta", precioVenta),
                                Updates.set("caracteristicas", caracteristicas),
                                Updates.set("estatus", estatus),
                                Updates.set("fechaActualizacion", getFechaActualizacion()),
                                Updates.set("imagenRuta", nuevaImagenRuta)
                        )
                );

                LOGGER.info("✅ Producto actualizado: " + codigoProducto);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente");
                limpiarCampos();
                return "menu_catalogo?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto no existe");
                return null;
            }

        } catch (Exception e) {
            LOGGER.severe("❌ Error al editar producto: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo editar el producto");
            return null;
        }
    }

    // ==============================================================  
    // MÉTODO: REGISTRAR PRODUCTO  
    // ==============================================================
    public String registrarProducto() {
        try {
            MongoDatabase db = getDatabase();
            MongoCollection<Document> productos = db.getCollection("productos");

            // Generar código único
            String nuevoCodigo = generarCodigoUnico();
            while (productos.find(Filters.eq("codigoProducto", nuevoCodigo)).first() != null) {
                nuevoCodigo = generarCodigoUnico();
            }

            // Guardar imagen
            if (imagen != null && imagen.getSize() > 0) {
                imagenRuta = guardarImagenFueraDelProyecto(imagen);
            } else {
                imagenRuta = "default.png";
            }

            // Crear documento
            Document nuevoProducto = new Document("codigoProducto", nuevoCodigo)
                    .append("nombre", nombre)
                    .append("marca", marca)
                    .append("categoria", categoria)
                    .append("precioCompra", precioCompra)
                    .append("precioVenta", precioVenta)
                    .append("caracteristicas", caracteristicas)
                    .append("estatus", estatus != null ? estatus : "Activo")
                    .append("fechaCreacion", getFechaCreacion())
                    .append("fechaActualizacion", getFechaActualizacion())
                    .append("imagenRuta", imagenRuta);

            productos.insertOne(nuevoProducto);

            LOGGER.info("✅ Producto registrado: " + nuevoCodigo + " | Imagen: " + imagenRuta);
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto registrado correctamente");

            limpiarCampos();
            return "menu_catalogo?faces-redirect=true";

        } catch (Exception e) {
            LOGGER.severe("❌ Error al registrar producto: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el producto");
            return null;
        }
    }

    // ==============================================================  
    // MÉTODO: GUARDAR IMAGEN FUERA DEL PROYECTO
    // ==============================================================
    private String guardarImagenFueraDelProyecto(Part imagen) throws IOException {
        if (imagen == null || imagen.getSize() == 0) {
            return "default.png";
        }

        try {
            // Obtener extensión
            String nombreOriginal = imagen.getSubmittedFileName();
            String extension = "";
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            // Generar nombre único
            String nombreArchivo = System.currentTimeMillis() + extension;
            Path destino = Paths.get(UPLOAD_DIR, nombreArchivo);

            LOGGER.info("💾 Guardando imagen en: " + destino.toAbsolutePath());

            // Copiar archivo
            try (InputStream input = imagen.getInputStream()) {
                Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            // Verificar
            if (Files.exists(destino)) {
                long tamaño = Files.size(destino);
                LOGGER.info("✅ Imagen guardada correctamente - Tamaño: " + tamaño + " bytes");
                return nombreArchivo; // Solo guardamos el nombre
            } else {
                throw new IOException("El archivo no se guardó correctamente");
            }

        } catch (Exception e) {
            LOGGER.severe("❌ Error al guardar imagen: " + e.getMessage());
            throw e;
        }
    }

    // ==============================================================  
    // MÉTODO: ELIMINAR IMAGEN FÍSICA
    // ==============================================================
    private void eliminarImagen(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.equals("default.png")) {
            return;
        }

        try {
            Path path = Paths.get(UPLOAD_DIR, nombreArchivo);
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.info("🗑️ Imagen eliminada: " + nombreArchivo);
            }
        } catch (IOException e) {
            LOGGER.warning("⚠ No se pudo eliminar imagen: " + e.getMessage());
        }
    }

    // ==============================================================  
    // MÉTODO: VERIFICAR SI IMAGEN EXISTE
    // ==============================================================
    public boolean isImagenExistente() {
        if (imagenRuta == null || imagenRuta.trim().isEmpty() || imagenRuta.equals("default.png")) {
            return false;
        }

        try {
            Path path = Paths.get(UPLOAD_DIR, imagenRuta);
            boolean existe = Files.exists(path);
            
            LOGGER.info("🔍 Verificando: " + path.toAbsolutePath() + " | Existe: " + existe);
            
            return existe;
        } catch (Exception e) {
            LOGGER.warning("⚠ Error verificando imagen: " + e.getMessage());
            return false;
        }
    }

    // ==============================================================  
    // MÉTODO: OBTENER RUTA COMPLETA DE IMAGEN (para debugging)
    // ==============================================================
    public String getRutaCompletaImagen() {
        if (imagenRuta == null || imagenRuta.isEmpty()) {
            return "No hay imagen";
        }
        return UPLOAD_DIR + File.separator + imagenRuta;
    }

    // ==============================================================  
    // MÉTODOS AUXILIARES  
    // ==============================================================
    private void limpiarCampos() {
        codigoBusqueda = null;
        codigoProducto = null;
        nombre = null;
        marca = null;
        categoria = null;
        precioCompra = 0.0;
        precioVenta = 0.0;
        caracteristicas = null;
        estatus = null;
        fechaCreacion = null;
        fechaActualizacion = null;
        imagen = null;
        imagenRuta = null;
    }

    public String cancelarRegistro() {
        limpiarCampos();
        return "menu_catalogo?faces-redirect=true";
    }

    private String generarCodigoUnico() {
        return "INT-" + (System.currentTimeMillis() / 1000);
    }

    private void addMessage(FacesMessage.Severity severity, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, titulo, detalle));
    }

    public String getFechaCreacion() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public String getFechaActualizacion() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    // ==============================================================  
    // GETTERS Y SETTERS  
    // ==============================================================
    public String getCodigoBusqueda() { return codigoBusqueda; }
    public void setCodigoBusqueda(String codigoBusqueda) { this.codigoBusqueda = codigoBusqueda; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public String getFechaCreacionStr() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacionStr() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Part getImagen() { return imagen; }
    public void setImagen(Part imagen) { this.imagen = imagen; }

    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }
}