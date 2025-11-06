package com.refaccionaria.proveedoresInternos.devoluciones;

import com.mongodb.client.*;
import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.models.VentaDetalle;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.Document;
public class VentaService {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static final String COLLECTION_NAME = "ventas";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> coleccionVentas;

    private static final Logger LOGGER = Logger.getLogger(VentaService.class.getName());

    public VentaService() {
        try {
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DB_NAME);
            coleccionVentas = database.getCollection(COLLECTION_NAME);
            LOGGER.info("Conexión a MongoDB exitosa.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con MongoDB", e);
        }
    }

    public String generarFolio() {
        long totalVentas = coleccionVentas.countDocuments();
        return String.format("V-2025-%04d", totalVentas + 1);
    }
public List<Venta> buscarVentas(String folio, String estado, Date fechaInicio) {
    List<Venta> resultados = new ArrayList<>();
    List<Bson> filtros = new ArrayList<>();

    if (folio != null && !folio.isEmpty())
        filtros.add(Filters.eq("folio_venta", folio));
    if (estado != null && !estado.isEmpty())
        filtros.add(Filters.eq("status", estado));
    if (fechaInicio != null)
        filtros.add(Filters.gte("fecha_venta", fechaInicio));

    Bson filtroFinal = filtros.isEmpty() ? new Document() : Filters.and(filtros);

    // 🔹 Recorremos las ventas
    for (Document doc : coleccionVentas.find(filtroFinal)) {
        Venta venta = new Venta();
        venta.setFolio_venta(doc.getString("folio_venta"));
        venta.setUsuario_id(doc.getString("usuario_id"));
        venta.setStatus(doc.getString("status"));
        venta.setTotal(doc.getDouble("total"));
        venta.setFecha_venta(doc.getDate("fecha_venta"));

        // 🔹 Convertir el array "detalle" (productos vendidos)
        List<Document> detalleDocs = doc.getList("detalle", Document.class);
        List<VentaDetalle> listaDetalles = new ArrayList<>();

        if (detalleDocs != null) {
            for (Document d : detalleDocs) {
                VentaDetalle det = new VentaDetalle();
                if (d.get("producto_id") != null)
                    det.setProducto_id(String.valueOf(d.get("producto_id")));
                det.setNombre(d.getString("nombre"));
                det.setCantidad(d.getInteger("cantidad", 0));
                det.setPrecio_unitario(d.getDouble("precio_unitario"));
                det.setSubtotal(d.getDouble("subtotal"));
                listaDetalles.add(det);
            }
        }

        venta.setDetalle(listaDetalles);
        resultados.add(venta);
    }

    return resultados;
}
    public void insertarVenta(Venta venta) {
        try {
            Document docVenta = new Document()
                    .append("folio_venta", venta.getFolio_venta())
                    .append("fecha_venta", Optional.ofNullable(venta.getFecha_venta()).orElse(new Date()))
                    .append("usuario_id", venta.getUsuario_id()) // guardamos string (username o hex)
                    .append("total", Optional.ofNullable(venta.getTotal()).orElse(0d))
                    .append("status", Optional.ofNullable(venta.getStatus()).orElse("Completada"));

            List<Document> detalleDocs = new ArrayList<>();
            if (venta.getDetalle() != null) {
                for (VentaDetalle d : venta.getDetalle()) {
                    Document det = new Document();
                    // si producto_id parece ObjectId hex válido, guardarlo como ObjectId; si no, como string
                    if (d.getProducto_id() != null && ObjectId.isValid(d.getProducto_id())) {
                        det.append("producto_id", new ObjectId(d.getProducto_id()));
                    } else {
                        det.append("producto_id", d.getProducto_id());
                    }
                    det.append("nombre", d.getNombre());
                    det.append("cantidad", d.getCantidad());
                    det.append("precio_unitario", d.getPrecio_unitario());
                    det.append("subtotal", d.getSubtotal());
                    detalleDocs.add(det);
                }
            }
            docVenta.append("detalle", detalleDocs);

            coleccionVentas.insertOne(docVenta);
            LOGGER.info("Venta registrada correctamente: " + venta.getFolio_venta());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al insertar venta", e);
            throw e;
        }
    }
    // ============================================================
    // 🔹 LISTAR TODAS LAS VENTAS (para reportes)
    // ============================================================
    public List<Venta> listarTodas() {
        List<Venta> ventas = new ArrayList<>();
        try {
            for (Document doc : coleccionVentas.find()) {
                Venta venta = new Venta();
                ObjectId id = doc.getObjectId("_id");
                if (id != null) venta.set_id(id.toHexString());
                venta.setFolio_venta(doc.getString("folio_venta"));
                venta.setFecha_venta(doc.getDate("fecha_venta"));
                venta.setUsuario_id(
                        doc.get("usuario_id") != null ? doc.get("usuario_id").toString() : "N/A");
                venta.setStatus(doc.getString("status"));
                Object rawTotal = doc.get("total");
                venta.setTotal(rawTotal instanceof Number ? ((Number) rawTotal).doubleValue() : 0d);
                ventas.add(venta);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al listar ventas", e);
        }
        return ventas;
    }

    public Venta buscarPorFolio(String folio) {
        try {
            Document doc = coleccionVentas.find(new Document("folio_venta", folio)).first();
            if (doc == null) return null;

            Venta venta = new Venta();
            ObjectId id = doc.getObjectId("_id");
            if (id != null) venta.set_id(id.toHexString());

            venta.setFolio_venta(doc.getString("folio_venta"));
            venta.setFecha_venta(doc.getDate("fecha_venta"));

            // usuario_id puede venir como ObjectId o String
            Object rawUser = doc.get("usuario_id");
            if (rawUser instanceof ObjectId) {
                venta.setUsuario_id(((ObjectId) rawUser).toHexString());
            } else if (rawUser != null) {
                venta.setUsuario_id(String.valueOf(rawUser));
            } else {
                venta.setUsuario_id(null);
            }

            Object rawTotal = doc.get("total");
            venta.setTotal(rawTotal instanceof Number ? ((Number) rawTotal).doubleValue() : 0d);
            venta.setStatus(doc.getString("status"));

            List<VentaDetalle> listaDetalles = new ArrayList<>();
            List<Document> detalles = (List<Document>) doc.get("detalle");
            if (detalles != null) {
                for (Document d : detalles) {
                    VentaDetalle det = new VentaDetalle();

                    Object rawProdId = d.get("producto_id");
                    if (rawProdId instanceof ObjectId) {
                        det.setProducto_id(((ObjectId) rawProdId).toHexString());
                    } else if (rawProdId != null) {
                        det.setProducto_id(String.valueOf(rawProdId));
                    }

                    det.setNombre(d.getString("nombre"));

                    Object rawCant = d.get("cantidad");
                    det.setCantidad(rawCant instanceof Number ? ((Number) rawCant).intValue() : 0);

                    Object rawPU = d.get("precio_unitario");
                    det.setPrecio_unitario(rawPU instanceof Number ? ((Number) rawPU).doubleValue() : 0d);

                    Object rawSub = d.get("subtotal");
                    det.setSubtotal(rawSub instanceof Number ? ((Number) rawSub).doubleValue() : 0d);

                    listaDetalles.add(det);
                }
            }
            venta.setDetalle(listaDetalles);
            return venta;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar venta", e);
            return null;
        }
    }

    public void cerrarConexion() {
        if (mongoClient != null) mongoClient.close();
    }
}
