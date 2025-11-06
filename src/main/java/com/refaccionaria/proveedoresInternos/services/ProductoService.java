package com.refaccionaria.proveedoresInternos.services;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductoService {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static final String COLLECTION_NAME = "productos";

    private final MongoClient client;
    private final MongoCollection<Document> col;

    public ProductoService() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        client = MongoClients.create(CONNECTION_STRING);
        MongoDatabase db = client.getDatabase(DB_NAME);
        col = db.getCollection(COLLECTION_NAME);
    }

    /**
     * Busca por:
     *  - _id (ObjectId hex)
     *  - codigoProducto (match exacto)
     *  - nombre (regex case-insensitive)
     */
    public Document buscarProducto(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return null;

        // 1) _id como ObjectId
        if (ObjectId.isValid(criterio)) {
            Document byId = col.find(new Document("_id", new ObjectId(criterio))).first();
            if (byId != null) return byId;
        }

        // 2) codigoProducto exacto
        Document byCode = col.find(new Document("codigoProducto", criterio)).first();
        if (byCode != null) return byCode;

        // 3) nombre regex (i)
        Document regex = new Document("$regex", criterio).append("$options", "i");
        return col.find(new Document("nombre", regex)).first();
    }

    public void close() {
        if (client != null) client.close();
    }
}
