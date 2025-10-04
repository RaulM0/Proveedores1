package com.refaccionaria.proveedoresInternos.api;

import com.refaccionaria.proveedoresInternos.beans.CatalogoBean;
import com.refaccionaria.proveedoresInternos.models.Producto;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 * @author RMD
 */
@Path("/catalogo") //Ruta del endpoint
public class CatalogoApi {

    @Inject
    private CatalogoBean catalogoBean; //Bean de JSf que maneja productos

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Producto> obtenerProductos() {
        return catalogoBean.getProductos();
    }

}
