package com.novastudent.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for generating professional PDF reports using iText 7.
 */
public class PDFExporter {

    private static final String EXPORT_DIR = "exports";
    private static final DeviceRgb BRAND_COLOR = new DeviceRgb(139, 92, 246); // Purple
    private static final DeviceRgb HEADER_BG = new DeviceRgb(248, 250, 252); // Light Gray

    /**
     * Generates a generic tabular PDF report.
     *
     * @param title Report title
     * @param subtitle Report subtitle or description
     * @param headers Table column headers
     * @param data 2D array of row data
     * @return The absolute path to the generated PDF
     */
    public static String exportTableReport(String title, String subtitle, String[] headers, String[][] data) throws IOException {
        Path exportDir = Paths.get(EXPORT_DIR);
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = title.replaceAll("\\s+", "_").toLowerCase() + "_" + timestamp + ".pdf";
        File file = exportDir.resolve(fileName).toFile();

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4.rotate())) {

            document.setMargins(36, 36, 36, 36);

            // Header Section
            Paragraph brandTitle = new Paragraph("NOVA STUDENT")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(BRAND_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(0);
            
            Paragraph reportTitle = new Paragraph(title)
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5)
                    .setMarginBottom(0);
            
            Paragraph reportSubtitle = new Paragraph(subtitle + " | Generated: " + DateUtil.formatDisplay(LocalDateTime.now()))
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);

            document.add(brandTitle);
            document.add(reportTitle);
            document.add(reportSubtitle);

            // Table setup
            float[] columnWidths = new float[headers.length];
            for (int i = 0; i < headers.length; i++) {
                columnWidths[i] = 1f; // Equal width columns
            }
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            // Table Headers
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(11))
                        .setBackgroundColor(HEADER_BG)
                        .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(6);
                table.addHeaderCell(cell);
            }

            // Table Data
            boolean alternate = false;
            for (String[] row : data) {
                for (String cellData : row) {
                    Cell cell = new Cell().add(new Paragraph(cellData != null ? cellData : "").setFontSize(10))
                            .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                            .setPadding(6);
                    if (alternate) {
                        cell.setBackgroundColor(new DeviceRgb(250, 250, 250));
                    }
                    table.addCell(cell);
                }
                alternate = !alternate;
            }

            document.add(table);
            
            // Footer
            Paragraph footer = new Paragraph("NovaStudent Smart Management System \u00A9 " + LocalDateTime.now().getYear())
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);
        }

        return file.getAbsolutePath();
    }
}
