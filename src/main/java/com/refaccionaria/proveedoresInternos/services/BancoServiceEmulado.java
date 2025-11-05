// Asegúrate de que este paquete coincida con tu estructura
package com.refaccionaria.proveedoresInternos.services;

import java.util.logging.Logger;

/**
 * Esta clase EMULA ser un endpoint bancario externo.
 * Finge una llamada de red y devuelve un "éxito" o "falla".
 */
public class BancoServiceEmulado {

    private static final Logger LOGGER = Logger.getLogger(BancoServiceEmulado.class.getName());

    /**
     * Simula la petición de una transferencia bancaria.
     * @param monto El dinero a transferir.
     * @param clabe La CLABE (o datos) del cliente.
     * @return true si la transferencia fue "exitosa", false si "falló".
     */
    public boolean realizarTransferencia(double monto, String clabe) {
        
        LOGGER.info("--- INICIO SIMULACIÓN BANCARIA ---");
        LOGGER.info("Conectando con el endpoint del banco...");
        
        // Simula el tiempo de espera de la red
        try {
            Thread.sleep(1500); // Espera 1.5 segundos
        } catch (InterruptedException e) {
            // No hacemos nada, solo era una pausa
        }

        // Simula una lógica de aprobación
        if (monto > 0 && clabe != null && clabe.length() == 18) {
            LOGGER.info("Transferencia APROBADA por $" + monto + " a la cuenta " + clabe);
            LOGGER.info("--- FIN SIMULACIÓN BANCARIA ---");
            return true;
        } else {
            LOGGER.warning("Transferencia RECHAZADA por el banco (datos inválidos).");
            LOGGER.info("--- FIN SIMULACIÓN BANCARIA ---");
            return false;
        }
    }
}