package com.refaccionaria.proveedoresInternos.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.logging.Logger;

@Named("productoBean") // Nombre usado en el XHTML: #{productoBean.propiedad}
@RequestScoped        // El bean vive solo durante el ciclo de vida de la petición
public class ProductoBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProductoBean.class.getName());

    // --- 1. Propiedades del Formulario (Inputs) ---
    private String nombre;
    private String marca;
    private String categoria;
    private double precioCompra;
    private double precioVenta;
    private String caracteristicas;
    private String estatus;

    // --- 2. Propiedades de Metadatos (Valores de solo lectura) ---
    // Usamos final para los valores estáticos del boceto
    private final String codigoProducto = "int-001";
    private final String fechaActualizacion = "01/12/2025";
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===========================================
    // MÉTODOS DE ACCIÓN (Lógica del Botón)
    // ===========================================
    /**
     * Maneja la lógica del botón 'Registrar Producto'.
     *
     * @return Regla de navegación.
     */
    public String registrarProducto() {
        // En un entorno de producción, aquí se haría la validación final 
        // y la llamada a un servicio para guardar los datos (ej. EJB o JPA).

        LOGGER.info("--- PRODUCTO RECIBIDO PARA REGISTRO ---");
        LOGGER.info("Nombre: " + nombre);
        LOGGER.info("Marca: " + marca);
        LOGGER.info("Precio Compra: " + precioCompra);
        LOGGER.info("Precio Venta: " + precioVenta);
        LOGGER.info("Características: " + caracteristicas);
        LOGGER.info("-------------------------------------");

        // Retorna la vista a la que se debe navegar
        // Si tienes una página llamada 'exito.xhtml', retornarías "exito?faces-redirect=true"
        return "dashboard?faces-redirect=true";
    }

    /**
     * Maneja la navegación del botón 'Cancelar' o 'Volver'.
     *
     * @return Regla de navegación.
     */
    public String navegacionVolver() {
        // Simplemente redirige sin procesar el formulario
        return "dashboard?faces-redirect=true";
    }
    
    /**
     * Obtiene la fecha actual del sistema y la formatea.
     * @return Fecha de hoy en formato DD/MM/AAAA.
     */
    public String getFechaCreacion() { 
        return LocalDate.now().format(DATE_FORMATTER);
    }

    // ===========================================
    // GETTERS Y SETTERS
    // ===========================================
    // Getters y Setters para las propiedades enlazadas
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    // Getters para Metadatos (propiedades de solo lectura)
    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }
}
