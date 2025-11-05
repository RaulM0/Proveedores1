package com.refaccionaria.proveedoresInternos.devoluciones;

import com.refaccionaria.proveedoresInternos.models.Devolucion;
import com.refaccionaria.proveedoresInternos.services.BancoServiceEmulado;
import com.refaccionaria.proveedoresInternos.services.DevolucionService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

import java.util.List;

@Named("procesarReembolsosBean")
@ViewScoped
public class ProcesarReembolsosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Devolucion> devolucionesPendientes;

    // Servicios que necesitamos
    private DevolucionService devolucionService;
    private BancoServiceEmulado bancoServiceEmulado;

    @PostConstruct
    public void init() {
        this.devolucionService = new DevolucionService();
        this.bancoServiceEmulado = new BancoServiceEmulado();
        // Carga la lista de devoluciones al abrir la página
        cargarPendientes();
    }

    /**
     * Llena la lista 'devolucionesPendientes' que usa la h:dataTable.
     */
    public void cargarPendientes() {
        try {
            this.devolucionesPendientes = devolucionService.getDevolucionesPendientes();
        } catch (Exception e) {
            addErrorMessage("Error de Carga", "No se pudieron cargar las devoluciones pendientes.");
            e.printStackTrace(); // Muestra el error en el log del servidor
        }
    }

    /**
     * Esta es la acción que llama el botón "Procesar Reembolso Bancario".
     *
     * @param devolucion El objeto de la fila seleccionada.
     */
    // EN: ProcesarReembolsosBean.java
    /**
     * Esta es la acción que llama el botón "Procesar Reembolso Bancario".
     *
     * @param devolucion El objeto de la fila seleccionada.
     */
    public void reembolsarABanco(Devolucion devolucion) {

        String clabeFalsaCliente = "123456789012345678";

        // 1. Llamar al "endpoint" emulado del banco
        boolean pagoExitoso = bancoServiceEmulado.realizarTransferencia(
                devolucion.getTotalReembolsado(),
                clabeFalsaCliente
        );

        if (pagoExitoso) {
            // 2. Si el banco "aprobó", actualizamos nuestro sistema

            // --- AQUÍ ESTÁ EL CAMBIO ---
            String nuevoEstado = "Procesada"; // <-- CAMBIADO (Usando tu estado)
            // --- FIN DEL CAMBIO ---

            boolean exito = devolucionService.actualizarEstado(devolucion.get_id(), nuevoEstado);

            if (exito) {
                addInfoMessage("¡Éxito!", "El banco procesó el reembolso para " + devolucion.getDevolucionId());
                cargarPendientes();
            } else {
                addErrorMessage("¡Error Crítico!", "El banco SÍ procesó el pago, pero NO se pudo actualizar el estado en la BD. ¡Revisión manual urgente!");
            }
        } else {
            // 3. Si el banco "rechazó"
            addErrorMessage("Rechazado", "El banco rechazó la transferencia para " + devolucion.getDevolucionId());
        }
    }

    // --- Getters y Setters ---
    public List<Devolucion> getDevolucionesPendientes() {
        return devolucionesPendientes;
    }

    public void setDevolucionesPendientes(List<Devolucion> devolucionesPendientes) {
        this.devolucionesPendientes = devolucionesPendientes;
    }

    // --- Métodos de ayuda para mensajes ---
    private void addInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
}
