package com.refaccionaria.proveedoresInternos.services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.refaccionaria.proveedoresInternos.models.Usuario;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class UsuarioService {

    private final MongoCollection<Usuario> coleccion;

    public UsuarioService() {
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
        this.coleccion = db.getCollection("usuarios", Usuario.class);
    }

    public List<Usuario> listarTodos() {
        return coleccion.find().into(new ArrayList<>());
    }

    public Usuario buscarPorUsuario(String usuario) {
        return coleccion.find(Filters.eq("usuario", usuario)).first();
    }

    public void guardar(Usuario usuario) {
        if (usuario.getId() == null)
            coleccion.insertOne(usuario);
        else
            coleccion.replaceOne(Filters.eq("_id", usuario.getId()), usuario);
    }

    public void eliminar(ObjectId id) {
        coleccion.deleteOne(Filters.eq("_id", id));
    }
}
