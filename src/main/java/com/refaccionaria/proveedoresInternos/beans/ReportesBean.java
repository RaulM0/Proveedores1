package com.refaccionaria.proveedoresInternos.beans;

import com.refaccionaria.proveedoresInternos.devoluciones.VentaService;
import com.refaccionaria.proveedoresInternos.models.Venta;
import com.refaccionaria.proveedoresInternos.services.PagoService;
import com.refaccionaria.proveedoresInternos.services.DevolucionService; // ✅ nuevo import
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

// ✅ Apache POI (Excel)
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// ✅ OpenPDF (PDF)
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// ✅ Color correcto
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

@Named("reportesBean")
@RequestScoped
public class ReportesBean {

    private final VentaService ventaService = new VentaService();
    private final PagoService pagoService = new PagoService();
    private final DevolucionService devolucionService = new DevolucionService(); // ✅ agregado

    // ============================================================
    // 🟢 1. GENERAR REPORTE DE VENTAS EN EXCEL
    // ============================================================
    public void generarVentasExcel() {
        try {
            List<Venta> ventas = ventaService.listarTodas();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Ventas");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            String[] columnas = { "Folio", "Usuario", "Fecha", "Estado", "Total" };
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int fila = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(v.getFolio_venta());
                row.createCell(1).setCellValue(v.getUsuario_id());
                row.createCell(2).setCellValue(v.getFecha_venta() != null ? v.getFecha_venta().toString() : "-");
                row.createCell(3).setCellValue(v.getStatus());
                row.createCell(4).setCellValue(v.getTotal());
            }

            for (int i = 0; i < columnas.length; i++)
                sheet.autoSizeColumn(i);

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().responseReset();
            fc.getExternalContext().setResponseContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            fc.getExternalContext().setResponseHeader(
                    "Content-Disposition", "attachment; filename=\"reporte_ventas.xlsx\"");

            OutputStream os = fc.getExternalContext().getResponseOutputStream();
            workbook.write(os);
            workbook.close();
            os.flush();
            os.close();
            fc.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // 🔴 2. GENERAR REPORTE DE VENTAS EN PDF
    // ============================================================
    public void generarVentasPDF() {
        try {
            List<Venta> ventas = ventaService.listarTodas();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("REPORTE DE VENTAS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(43, 109, 117)));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2, 2, 3, 2, 2 });

            String[] headers = { "Folio", "Usuario", "Fecha", "Estado", "Total" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (Venta v : ventas) {
                table.addCell(v.getFolio_venta());
                table.addCell(v.getUsuario_id());
                table.addCell(v.getFecha_venta() != null ? v.getFecha_venta().toString() : "-");
                table.addCell(v.getStatus());
                table.addCell(String.format("$%.2f", v.getTotal()));
            }

            document.add(table);
            document.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().responseReset();
            fc.getExternalContext().setResponseContentType("application/pdf");
            fc.getExternalContext().setResponseHeader("Content-Disposition",
                    "attachment; filename=\"reporte_ventas.pdf\"");
            OutputStream os = fc.getExternalContext().getResponseOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
            fc.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // 🟡 3. GENERAR REPORTE DE PAGOS EN PDF
    // ============================================================
    public void generarPagosPDF() {
        try {
            List<org.bson.Document> pagos = pagoService.listarPagos();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("REPORTE DE PAGOS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(43, 109, 117)));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2, 2, 2, 2, 2, 3 });

            String[] headers = { "Folio Venta", "Método", "Fecha", "Monto", "Estado", "Referencia Banco" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (org.bson.Document p : pagos) {
                String folio = p.getString("folioVenta") != null ? p.getString("folioVenta") : "-";
                String metodo = p.getString("metodoPago") != null ? p.getString("metodoPago") : "-";
                String fecha = p.getDate("fecha") != null ? p.getDate("fecha").toString() : "-";
                String estado = p.getString("estado") != null ? p.getString("estado") : "-";
                String ref = p.getString("referenciaBanco") != null ? p.getString("referenciaBanco") : "-";

                Object montoObj = p.get("monto");
                String monto = (montoObj instanceof Number)
                        ? String.format("$%.2f", ((Number) montoObj).doubleValue())
                        : "$0.00";

                table.addCell(folio);
                table.addCell(metodo);
                table.addCell(fecha);
                table.addCell(monto);
                table.addCell(estado);
                table.addCell(ref);
            }

            document.add(table);
            document.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().responseReset();
            fc.getExternalContext().setResponseContentType("application/pdf");
            fc.getExternalContext().setResponseHeader("Content-Disposition",
                    "attachment; filename=\"reporte_pagos.pdf\"");
            OutputStream os = fc.getExternalContext().getResponseOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
            fc.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // 🔵 4. GENERAR REPORTE DE DEVOLUCIONES EN PDF
    // ============================================================
    public void generarDevolucionesPDF() {
        try {
            List<org.bson.Document> devoluciones = devolucionService.listarDevoluciones();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("REPORTE DE DEVOLUCIONES",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(43, 109, 117)));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2, 2, 2, 3, 2, 2 });

            String[] headers = { "ID Devolución", "Folio Venta", "Motivo", "Estado", "Fecha", "Total Reembolsado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (org.bson.Document d : devoluciones) {
                String idDev = d.getString("devolucionId") != null ? d.getString("devolucionId") : "-";
                String folioVenta = d.getString("folioVenta") != null ? d.getString("folioVenta") : "-";
                String motivo = d.getString("motivo") != null ? d.getString("motivo") : "-";
                String estado = d.getString("estado") != null ? d.getString("estado") : "-";
                String fecha = d.getDate("fecha") != null ? d.getDate("fecha").toString() : "-";

                Object montoObj = d.get("totalReembolsado");
                String total = (montoObj instanceof Number)
                        ? String.format("$%.2f", ((Number) montoObj).doubleValue())
                        : "$0.00";

                table.addCell(idDev);
                table.addCell(folioVenta);
                table.addCell(motivo);
                table.addCell(estado);
                table.addCell(fecha);
                table.addCell(total);
            }

            document.add(table);
            document.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().responseReset();
            fc.getExternalContext().setResponseContentType("application/pdf");
            fc.getExternalContext().setResponseHeader("Content-Disposition",
                    "attachment; filename=\"reporte_devoluciones.pdf\"");
            OutputStream os = fc.getExternalContext().getResponseOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
            fc.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
