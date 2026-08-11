package com.novastudent.util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for exporting data to CSV files.
 * Creates files in the exports/ directory.
 */
public class CSVExporter {

    private static final String EXPORT_DIR = "exports";
    private static final String DELIMITER = ",";

    /**
     * Exports data to a CSV file.
     *
     * @param filePrefix Prefix for the filename (e.g., "student_list")
     * @param headers Column headers
     * @param data 2D array of row data
     * @return The absolute path of the exported file
     */
    public static String export(String filePrefix, String[] headers, String[][] data) throws IOException {
        // Ensure exports directory exists
        Path exportDir = Paths.get(EXPORT_DIR);
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = filePrefix + "_" + timestamp + ".csv";
        Path filePath = exportDir.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Write BOM for Excel compatibility
            writer.write('\uFEFF');

            // Write headers
            writer.write(String.join(DELIMITER, headers));
            writer.newLine();

            // Write data rows
            for (String[] row : data) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) sb.append(DELIMITER);
                    sb.append(escapeCSV(row[i]));
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }

        return filePath.toAbsolutePath().toString();
    }

    /**
     * Escapes a CSV field value.
     * Wraps in quotes if it contains commas, quotes, or newlines.
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
