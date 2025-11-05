package com.refaccionaria.proveedoresInternos.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Servlet para servir imágenes almacenadas fuera del proyecto
 */
@WebServlet("/imagenes/*")
public class ImagenServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ImagenServlet.class.getName());
    
    // 📁 Misma ruta que en ProductoBean
    private static final String UPLOAD_DIR = System.getProperty("user.home") + 
            File.separator + "uploads" + 
            File.separator + "refaccionaria" + 
            File.separator + "productos";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el nombre del archivo de la URL
        // Ejemplo: /imagenes/12345678.jpg -> 12345678.jpg
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo no especificado");
            return;
        }

        String nombreArchivo = pathInfo.substring(1); // Quitar el "/"
        
        // Construir ruta completa
        Path rutaArchivo = Paths.get(UPLOAD_DIR, nombreArchivo);
        
        LOGGER.info("📷 Solicitando imagen: " + nombreArchivo);
        LOGGER.info("📁 Ruta completa: " + rutaArchivo.toAbsolutePath());

        // Verificar que el archivo existe
        if (!Files.exists(rutaArchivo)) {
            LOGGER.warning("⚠ Imagen no encontrada: " + rutaArchivo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }

        // Verificar que es un archivo (no un directorio)
        if (!Files.isRegularFile(rutaArchivo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta inválida");
            return;
        }

        // Determinar el tipo MIME
        String mimeType = getServletContext().getMimeType(nombreArchivo);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Tipo genérico
        }

        response.setContentType(mimeType);
        response.setContentLengthLong(Files.size(rutaArchivo));

        // Cache headers (opcional, para mejor rendimiento)
        response.setHeader("Cache-Control", "public, max-age=31536000"); // 1 año
        response.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);

        // Enviar el archivo
        try (OutputStream out = response.getOutputStream()) {
            Files.copy(rutaArchivo, out);
            out.flush();
            LOGGER.info("✅ Imagen servida correctamente: " + nombreArchivo);
        } catch (IOException e) {
            LOGGER.severe("❌ Error al servir imagen: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet para servir imágenes de productos almacenadas fuera del proyecto";
    }
}