package com.refaccionaria.proveedoresInternos;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List; // ✅ Esta es la correcta
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


@Named("pagosReportBean")
@SessionScoped
public class PagosReportBean implements Serializable {

    private String fechaInicio;
    private String fechaFin;
    private String estadoSeleccionado;

    private List<Map<String, Object>> listaPagos;
    private List<Map<String, Object>> listaFiltrada;

    public PagosReportBean() {
        cargarDatosSimulados();
    }

    private void cargarDatosSimulados() {
        listaPagos = new ArrayList<>();

        listaPagos.add(Map.of("referencia", "P001", "monto", 1500.00, "fecha", "2025-09-20", "estado", "Completado"));
        listaPagos.add(Map.of("referencia", "P002", "monto", 900.00, "fecha", "2025-09-25", "estado", "Pendiente"));
        listaPagos.add(Map.of("referencia", "P003", "monto", 1200.00, "fecha", "2025-09-28", "estado", "Completado"));
        listaPagos.add(Map.of("referencia", "P004", "monto", 750.00, "fecha", "2025-10-01", "estado", "Cancelado"));
        listaPagos.add(Map.of("referencia", "P005", "monto", 1800.00, "fecha", "2025-10-03", "estado", "Completado"));

        listaFiltrada = new ArrayList<>(listaPagos);
    }

    // === GETTERS & SETTERS ===
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getEstadoSeleccionado() { return estadoSeleccionado; }
    public void setEstadoSeleccionado(String estadoSeleccionado) { this.estadoSeleccionado = estadoSeleccionado; }

    public List<Map<String, Object>> getListaFiltrada() { return listaFiltrada; }

    // === FUNCIONALIDADES ===
    public void consultar() {
        listaFiltrada = listaPagos.stream()
                .filter(p -> estadoSeleccionado == null || estadoSeleccionado.isEmpty() ||
                        p.get("estado").equals(estadoSeleccionado))
                .collect(Collectors.toList());
        System.out.println("Consulta realizada. Resultados: " + listaFiltrada.size());
    }

    public void exportarPDF() {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream("ReportePagos.pdf"));
            document.open();

            document.add(new Paragraph("Reporte de Pagos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph("Fecha: " + new Date()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Referencia");
            table.addCell("Monto");
            table.addCell("Fecha");
            table.addCell("Estado");

            for (Map<String, Object> pago : listaFiltrada) {
                table.addCell(pago.get("referencia").toString());
                table.addCell("$" + pago.get("monto").toString());
                table.addCell(pago.get("fecha").toString());
                table.addCell(pago.get("estado").toString());
            }

            document.add(table);
            document.add(new Paragraph("\nTOTAL PAGOS REGISTRADOS: " + listaFiltrada.size()));
            document.close();

            System.out.println("✅ PDF generado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportarExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pagos");
            Row header = sheet.createRow(0);

            String[] columnas = {"Referencia", "Monto", "Fecha", "Estado"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowNum = 1;
            for (Map<String, Object> pago : listaFiltrada) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pago.get("referencia").toString());
                row.createCell(1).setCellValue(pago.get("monto").toString());
                row.createCell(2).setCellValue(pago.get("fecha").toString());
                row.createCell(3).setCellValue(pago.get("estado").toString());
            }

            try (FileOutputStream fileOut = new FileOutputStream("ReportePagos.xlsx")) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Excel generado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
