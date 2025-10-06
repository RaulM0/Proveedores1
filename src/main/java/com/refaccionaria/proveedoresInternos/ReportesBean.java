package com.refaccionaria.proveedoresInternos;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Named("reportesBean")
@SessionScoped
public class ReportesBean implements Serializable {

    private List<Map<String, Object>> listaVentas;
private String fechaInicio;
private String fechaFin;
private String vendedorSeleccionado;
private double totalVentas;
// 🔹 Filtros para los reportes

private String estadoSeleccionado;

// Getters y Setters
public String getFechaInicio() {
    return fechaInicio;
}

public void setFechaInicio(String fechaInicio) {
    this.fechaInicio = fechaInicio;
}

public String getFechaFin() {
    return fechaFin;
}

public void setFechaFin(String fechaFin) {
    this.fechaFin = fechaFin;
}

public String getEstadoSeleccionado() {
    return estadoSeleccionado;
}

public void setEstadoSeleccionado(String estadoSeleccionado) {
    this.estadoSeleccionado = estadoSeleccionado;
}

public void consultarVentas() {
    // Simulación de filtrado
    totalVentas = listaVentas.stream()
        .mapToDouble(v -> (Double) v.get("total"))
        .sum();
}

public double getTotalVentas() { return totalVentas; }

public String getVendedorSeleccionado() { return vendedorSeleccionado; }
public void setVendedorSeleccionado(String vendedorSeleccionado) { this.vendedorSeleccionado = vendedorSeleccionado; }

    public ReportesBean() {
        cargarDatosSimulados();
    }

    // 🔹 Simula algunos datos de ejemplo
    private void cargarDatosSimulados() {
        listaVentas = new ArrayList<>();

        listaVentas.add(Map.of("folio", "V001", "vendedor", "Carlos Pérez", "total", 2500.0, "estado", "Entregado"));
        listaVentas.add(Map.of("folio", "V002", "vendedor", "Ana López", "total", 1800.0, "estado", "Pendiente"));
        listaVentas.add(Map.of("folio", "V003", "vendedor", "Luis Torres", "total", 3200.0, "estado", "Entregado"));
    }

    public List<Map<String, Object>> getListaVentas() {
        return listaVentas;
    }

    // ✅ Exportar a PDF
    public void exportarPDF() {
        try {
            Document document = new Document(PageSize.A4);
            String filePath = System.getProperty("user.home") + "/Downloads/ReporteVentas.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            document.open();
            document.add(new Paragraph("📊 Reporte de Ventas"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Folio");
            table.addCell("Vendedor");
            table.addCell("Total");
            table.addCell("Estado");

            for (Map<String, Object> venta : listaVentas) {
                table.addCell(venta.get("folio").toString());
                table.addCell(venta.get("vendedor").toString());
                table.addCell(venta.get("total").toString());
                table.addCell(venta.get("estado").toString());
            }

            document.add(table);
            document.close();

            System.out.println("✅ PDF generado correctamente en: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Exportar a Excel
    public void exportarExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventas");
            Row header = sheet.createRow(0);
            String[] columnas = {"Folio", "Vendedor", "Total", "Estado"};

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
            }

            int fila = 1;
            for (Map<String, Object> venta : listaVentas) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(venta.get("folio").toString());
                row.createCell(1).setCellValue(venta.get("vendedor").toString());
                row.createCell(2).setCellValue(Double.parseDouble(venta.get("total").toString()));
                row.createCell(3).setCellValue(venta.get("estado").toString());
            }

            String filePath = System.getProperty("user.home") + "/Downloads/ReporteVentas.xlsx";
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();

            System.out.println("✅ Excel generado correctamente en: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
