package com.refaccionaria.proveedoresInternos.services;

import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagoService {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "refaccionaria";
    private static final String COLLECTION_NAME = "pagos";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> coleccionPagos;

    public PagoService() {
        try {
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DB_NAME);
            coleccionPagos = database.getCollection(COLLECTION_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Document> listarPagos() {
        List<Document> lista = new ArrayList<>();
        try {
            FindIterable<Document> docs = coleccionPagos.find().sort(Sorts.descending("fecha_pago"));
            for (Document doc : docs) {
                lista.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
