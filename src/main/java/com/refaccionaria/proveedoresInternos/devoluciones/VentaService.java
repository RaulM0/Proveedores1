package com.refaccionaria.proveedoresInternos.devoluciones;

import com.mongodb.client.*;
import com.refaccionaria.proveedoresInternos.models.VentaDetalle;
import com.refaccionaria.proveedoresInternos.models.Venta;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;

/**
 * --- SERVICIO REAL DE VENTA CON MONGODB --- Se conecta a la base de datos
 * 'refaccionaria' en MongoDB.
 */
public class VentaService {

    // --- CONFIGURACIÓN DE CONEXIÓN ---
    // ¡¡IMPORTANTE!! Ajusta esta línea si tu Mongo tiene usuario/contraseña
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static final String COLLECTION_NAME = "ventas";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> coleccionVentas;

    private static final Logger LOGGER = Logger.getLogger(VentaService.class.getName());

    public VentaService() {
        try {
            // Desactiva el logging detallado del driver de Mongo
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

            this.mongoClient = MongoClients.create(CONNECTION_STRING);
            this.database = mongoClient.getDatabase(DB_NAME);
            this.coleccionVentas = database.getCollection(COLLECTION_NAME);

            LOGGER.info("Conexión a MongoDB (" + DB_NAME + ") exitosa.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con MongoDB", e);
        }
    }

    /**
     * Busca una Venta en la colección 'ventas' usando el campo 'folio_venta'.
     */
    public Venta buscarPorFolio(String folio) {
        if (this.coleccionVentas == null) {
            LOGGER.severe("La colección de ventas no está inicializada. Revisa la conexión a MongoDB.");
            return null;
        }

        try {
            Document filtro = new Document("folio_venta", folio);
            Document ventaDoc = coleccionVentas.find(filtro).first();

            if (ventaDoc != null) {
                LOGGER.info("Venta encontrada: " + ventaDoc.toJson());
                return mapDocumentToVenta(ventaDoc);
            } else {
                LOGGER.warning("No se encontró venta con folio: " + folio);
                return null;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el folio: " + folio, e);
            return null;
        }
    }

    /**
     * Mapeador manual de un Document BSON a un POJO Venta. (Versión corregida
     * para manejar Integers y Doubles de forma segura)
     */
    private Venta mapDocumentToVenta(Document doc) {
        Venta venta = new Venta();

        // --- Mapeo de campos simples ---
        venta.set_id(doc.getObjectId("_id").toString());
        venta.setFolio_venta(doc.getString("folio_venta"));
        venta.setFecha_venta(doc.getDate("fecha_venta"));
        venta.setUsuario_id(doc.getObjectId("usuario_id").toString());
        venta.setStatus(doc.getString("status"));

        // --- INICIO DE LA CORRECCIÓN ---
        // Lee "total" como un tipo genérico 'Number'
        // Esto falla en la línea 88 de tu stack trace
        Number totalNum = doc.get("total", Number.class);
        if (totalNum != null) {
            // Convierte el 'Number' a 'double' (lo que espera tu POJO)
            venta.setTotal(totalNum.doubleValue());
        }
        // --- FIN DE LA CORRECCIÓN ---

        // --- Mapeo del sub-documento (lista) 'detalle' ---
        List<Document> detalleDocs = doc.getList("detalle", Document.class);
        List<VentaDetalle> detalleList = new ArrayList<>();

        if (detalleDocs != null) {
            for (Document detalleDoc : detalleDocs) {
                VentaDetalle detalle = new VentaDetalle();

                detalle.setProducto_id(detalleDoc.getObjectId("producto_id").toString());
                detalle.setNombre(detalleDoc.getString("nombre"));

                // --- APLICANDO LA MISMA LÓGICA SEGURA AQUÍ ---
                // Campo 'cantidad' (leído como Number y convertido a int)
                Number cantidadNum = detalleDoc.get("cantidad", Number.class);
                if (cantidadNum != null) {
                    detalle.setCantidad(cantidadNum.intValue());
                }

                // Campo 'precio_unitario' (leído como Number y convertido a double)
                Number precioNum = detalleDoc.get("precio_unitario", Number.class);
                if (precioNum != null) {
                    detalle.setPrecio_unitario(precioNum.doubleValue());
                }

                // Campo 'subtotal' (leído como Number y convertido a double)
                Number subtotalNum = detalleDoc.get("subtotal", Number.class);
                if (subtotalNum != null) {
                    detalle.setSubtotal(subtotalNum.doubleValue());
                }

                detalleList.add(detalle);
            }
        }

        venta.setDetalle(detalleList);

        return venta;
    }

    // (Opcional) Método para cerrar la conexión 
    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            LOGGER.info("Conexión a MongoDB cerrada.");
        }
    }
}
