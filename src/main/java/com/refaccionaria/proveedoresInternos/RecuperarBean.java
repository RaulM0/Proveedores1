package com.refaccionaria.proveedoresInternos;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

@Named("recuperarBean")
@RequestScoped
public class RecuperarBean implements Serializable {

    private String correo;

    public String enviar() {
        try {
            if (correo == null || correo.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", "Debe ingresar un correo electrónico."));
                return null;
            }

            // Simulación de envío de correo (sin BD)
            System.out.println("Simulación de recuperación enviada a: " + correo);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Recuperación enviada", 
                            "Se ha enviado un enlace de recuperación a " + correo));

            return null; // se queda en la misma página
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "No se pudo procesar la solicitud."));
            return null;
        }
    }

    // Getter & Setter
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
