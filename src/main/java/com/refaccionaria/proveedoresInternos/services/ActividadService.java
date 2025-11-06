package com.refaccionaria.proveedoresInternos.services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import com.refaccionaria.proveedoresInternos.models.Actividad;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.util.ArrayList;
import java.util.List;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ActividadService {

    private final MongoCollection<Actividad> coleccion;

    public ActividadService() {
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(pojoCodecRegistry)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase("refaccionaria");
        this.coleccion = db.getCollection("actividades", Actividad.class);
    }

    public void registrarActividad(String operacion, String usuario) {
        coleccion.insertOne(new Actividad(operacion, usuario));
    }

    public List<Actividad> listarTodas() {
        return coleccion.find()
                .sort(Sorts.descending("fecha"))
                .into(new ArrayList<>());
    }
}
