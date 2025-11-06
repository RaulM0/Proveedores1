package com.refaccionaria.proveedoresInternos.usuarios;

import com.refaccionaria.proveedoresInternos.models.Actividad;
import com.refaccionaria.proveedoresInternos.services.ActividadService;
import com.refaccionaria.proveedoresInternos.models.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named("registroActividadBean")
@SessionScoped
public class RegistroActividadBean implements Serializable {

    private final ActividadService servicio = new ActividadService();
    private List<Actividad> listaActividades;

    public RegistroActividadBean() {
        cargarActividades();
    }

    public void cargarActividades() {
        listaActividades = servicio.listarTodas();
    }

    // 🔒 Solo admin puede acceder
    public void verificarAccesoAdmin() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String, Object> session = ctx.getExternalContext().getSessionMap();
        Usuario usuarioActual = (Usuario) session.get("usuarioActual");

        if (usuarioActual == null || !"ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
            try {
                ctx.getExternalContext().redirect(ctx.getExternalContext().getRequestContextPath() + "/dashboard.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Actividad> getListaActividades() { return listaActividades; }
}
