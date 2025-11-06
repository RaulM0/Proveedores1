// Asegúrate de que este paquete coincida con tu estructura
package com.refaccionaria.proveedoresInternos.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
// Importa los modelos desde tu paquete de modelos
import com.refaccionaria.proveedoresInternos.models.Devolucion;
import com.refaccionaria.proveedoresInternos.models.DevolucionDetalle;
import java.util.ArrayList;
import java.util.Calendar;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date; // Importa java.util.Date
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bson.conversions.Bson;

/**
 * Servicio para manejar la lógica de negocio de las devoluciones. 1. Guarda la
 * devolución. 2. Actualiza el inventario.
 */
public class DevolucionService {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";

    private MongoClient mongoClient;
    private MongoDatabase database;

    // --- Colecciones ---
    private MongoCollection<Document> coleccionDevoluciones;
    private MongoCollection<Document> coleccionInventarios; // Para actualizar stock
    private MongoCollection<Document> coleccionProductos;   // Para buscar el codigoProducto

    private static final Logger LOGGER = Logger.getLogger(DevolucionService.class.getName());

    public DevolucionService() {
        try {
            // Silencia el ruidoso log del driver de Mongo
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

            this.mongoClient = MongoClients.create(CONNECTION_STRING);
            this.database = mongoClient.getDatabase(DB_NAME);

            // Inicializa las 3 colecciones que necesitamos
            this.coleccionDevoluciones = database.getCollection("devoluciones");
            this.coleccionInventarios = database.getCollection("inventario"); // Tu nombre de colección
            this.coleccionProductos = database.getCollection("productos");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con MongoDB", e);
        }
    }

    /**
     * Guarda la devolución en la BD y actualiza el inventario. Este es el
     * método principal que llama el Bean.
     */
    public boolean registrarDevolucion(Devolucion devolucion) {
        try {
            // --- 1. GUARDAR LA DEVOLUCIÓN ---
            Document docDevolucion = mapDevolucionToDocument(devolucion);
            coleccionDevoluciones.insertOne(docDevolucion);
            LOGGER.info("Devolución guardada con ID de Folio: " + devolucion.getDevolucionId());

            // --- 2. ACTUALIZAR INVENTARIO (LÓGICA DE 2 PASOS) ---
            for (DevolucionDetalle detalle : devolucion.getDetalle()) {

                ObjectId productoIdObj = new ObjectId(detalle.getProducto_id());
                int cantidadDevuelta = detalle.getCantidadDevuelta();

                // --- INICIO DE LA LÓGICA DE "SALTO" DE IDS ---
                // Paso 2a: Buscar en 'productos' usando el ObjectId
                Document productoDoc = coleccionProductos.find(Filters.eq("_id", productoIdObj)).first();

                if (productoDoc == null) {
                    LOGGER.warning("No se encontró el producto con ID: " + productoIdObj + " en la colección 'productos'. No se puede actualizar el stock.");
                    continue; // Salta al siguiente item del bucle
                }

                // Paso 2b: Extraer el 'codigoProducto'
                String codigoProducto = productoDoc.getString("codigoProducto");

                if (codigoProducto == null || codigoProducto.isEmpty()) {
                    LOGGER.warning("El producto con ID: " + productoIdObj + " no tiene un 'codigoProducto'. No se puede actualizar stock.");
                    continue;
                }

                // Paso 2c: Usar el 'codigoProducto' para actualizar 'inventario'
                coleccionInventarios.updateOne(
                        Filters.eq("codigoProducto", codigoProducto), // Filtro por el String 'INT-...'
                        Updates.combine(
                                Updates.inc("stock", cantidadDevuelta), // Suma al stock
                                Updates.set("fecha_actualizacion", new Date()) // Actualiza la fecha
                        )
                );
                // --- FIN DE LA LÓGICA DE "SALTO" ---

                LOGGER.info("Stock actualizado en 'inventario': " + codigoProducto + " (+" + cantidadDevuelta + ")");
            }

            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al registrar la devolución o actualizar stock", e);
            // En un sistema real, aquí se debería implementar una "reversión" (transacción)
            // para no dejar la data inconsistente (ej. guardar devolución pero fallar al actualizar stock).
            return false;
        }
    }

    /**
     * Mapeador privado para convertir el objeto Devolucion (POJO) a un
     * Documento BSON listo para MongoDB.
     */
    private Document mapDevolucionToDocument(Devolucion devolucion) {
        Document doc = new Document();

        doc.append("devolucionId", devolucion.getDevolucionId());
        doc.append("ventaId", new ObjectId(devolucion.getVentaId())); // Guarda como ObjectId
        doc.append("folioVenta", devolucion.getFolioVenta());
        doc.append("fecha", devolucion.getFecha()); // Guarda como ISODate
        doc.append("motivo", devolucion.getMotivo());
        doc.append("estado", devolucion.getEstado());
        doc.append("totalReembolsado", devolucion.getTotalReembolsado()); // Guarda como Double

        // Mapear la lista de detalle anidada
        List<Document> detalleDocs = devolucion.getDetalle().stream().map(det
                -> new Document("producto_id", new ObjectId(det.getProducto_id()))
                        .append("nombre", det.getNombre())
                        .append("cantidadDevuelta", det.getCantidadDevuelta())
                        .append("precioUnitario", det.getPrecioUnitario())
                        .append("subtotalDevuelto", det.getSubtotalDevuelto())
        ).collect(Collectors.toList());

        doc.append("detalle", detalleDocs);
        return doc;
    }

    /**
     * (NUEVO) Busca todas las devoluciones que esperan aprobación financiera.
     * Es llamado por el ProcesarReembolsosBean.
     */
    public List<Devolucion> getDevolucionesPendientes() {
        List<Devolucion> lista = new ArrayList<>();

        // --- AQUÍ ESTÁ EL CAMBIO ---
        // Filtro: { "estado": "Pendiente" }
        MongoCursor<Document> cursor = coleccionDevoluciones.find(
                Filters.eq("estado", "Pendiente") // <--- CAMBIADO
        ).iterator();
        // --- FIN DEL CAMBIO ---

        try {
            while (cursor.hasNext()) {
                lista.add(mapDocumentToDevolucion(cursor.next()));
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    /**
     * (NUEVO) Actualiza únicamente el estado de una devolución. Es llamado por
     * el ProcesarReembolsosBean después de un reembolso exitoso.
     */
    public boolean actualizarEstado(String devolucionMongoId, String nuevoEstado) {
        try {
            ObjectId objectId = new ObjectId(devolucionMongoId);

            coleccionDevoluciones.updateOne(
                    Filters.eq("_id", objectId), // Filtro
                    Updates.set("estado", nuevoEstado) // La acción
            );
            LOGGER.info("Estado de devolución " + devolucionMongoId + " actualizado a: " + nuevoEstado);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No se pudo actualizar el estado de la devolución " + devolucionMongoId, e);
            return false;
        }
    }

    /**
     * (NUEVO) Mapeador privado para convertir un Documento a Devolucion (POJO).
     * Necesario para getDevolucionesPendientes().
     */
    private Devolucion mapDocumentToDevolucion(Document doc) {
        Devolucion dev = new Devolucion();

        dev.set_id(doc.getObjectId("_id").toString());
        dev.setDevolucionId(doc.getString("devolucionId"));
        dev.setVentaId(doc.getObjectId("ventaId").toString());
        dev.setFolioVenta(doc.getString("folioVenta"));
        dev.setFecha(doc.getDate("fecha"));
        dev.setMotivo(doc.getString("motivo"));
        dev.setEstado(doc.getString("estado"));

        // Lee el número de forma segura (corrigiendo el error de Integers)
        Number total = doc.get("totalReembolsado", Number.class);
        if (total != null) {
            dev.setTotalReembolsado(total.doubleValue());
        }

        // Nota: No mapeamos el 'detalle' aquí porque la tabla
        // principal no lo necesita. Es más eficiente así.
        return dev;
    }

    /**
     * (NUEVO) Busca en el historial de devoluciones usando filtros dinámicos.
     * Llamado por el ConsultaDevolucionesBean.
     */
    public List<Devolucion> buscarDevoluciones(String folio, String estado, Date fechaDesde, Date fechaHasta) {
        
        List<Devolucion> lista = new ArrayList<>();
        List<Bson> filtros = new ArrayList<>(); // Lista para construir la consulta

        // --- Construcción de la consulta dinámica ---

        // 1. Filtro por Folio (Busca en dos campos: folioVenta o devolucionId)
        if (folio != null && !folio.trim().isEmpty()) {
            filtros.add(Filters.or(
                Filters.regex("devolucionId", folio, "i"), // "i" para ignorar mayúsc/minúsc
                Filters.regex("folioVenta", folio, "i")
            ));
        }
        
        // 2. Filtro por Estado
        if (estado != null && !estado.trim().isEmpty()) {
            filtros.add(Filters.eq("estado", estado));
        }

        // 3. Filtro por Fecha Desde (>=)
        if (fechaDesde != null) {
            filtros.add(Filters.gte("fecha", fechaDesde)); // gte = Greater Than or Equal
        }

        // 4. Filtro por Fecha Hasta (<=)
        if (fechaHasta != null) {
            // Ajuste para incluir el día completo (hasta las 23:59:59)
            Calendar c = Calendar.getInstance();
            c.setTime(fechaHasta);
            c.add(Calendar.DAY_OF_MONTH, 1); // Se mueve al siguiente día
            c.add(Calendar.MILLISECOND, -1); // Retrocede 1 milisegundo (23:59:59.999)
            
            filtros.add(Filters.lte("fecha", c.getTime())); // lte = Less Than or Equal
        }

        // --- Ejecución de la consulta ---
        MongoCursor<Document> cursor;
        
        if (filtros.isEmpty()) {
            // Si no hay filtros, trae todo (o limita los resultados)
            cursor = coleccionDevoluciones.find()
                    .sort(Sorts.descending("fecha")) // Ordena por fecha descendente
                    .limit(100) // Limita a 100 para no saturar
                    .iterator();
        } else {
            // Si hay filtros, los combina con AND
            cursor = coleccionDevoluciones.find(Filters.and(filtros))
                    .sort(Sorts.descending("fecha"))
                    .limit(100)
                    .iterator();
        }

        // --- Mapeo de resultados ---
        try {
            while (cursor.hasNext()) {
                // Reutilizamos el mapeador que ya teníamos
                lista.add(mapDocumentToDevolucion(cursor.next()));
            }
        } finally {
            cursor.close();
        }
        
        return lista;
    }
    public List<org.bson.Document> listarDevoluciones() {
    List<org.bson.Document> lista = new ArrayList<>();
    try (MongoCursor<org.bson.Document> cursor = coleccionDevoluciones
            .find()
            .sort(Sorts.descending("fecha"))
            .iterator()) {

        while (cursor.hasNext()) {
            lista.add(cursor.next());
        }

    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error al listar devoluciones para reporte", e);
    }
    return lista;
}
}
