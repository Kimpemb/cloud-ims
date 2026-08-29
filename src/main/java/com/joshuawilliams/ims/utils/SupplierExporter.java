package com.joshuawilliams.ims.utils;

import com.joshuawilliams.ims.model.Supplier;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for exporting supplier data to various formats (Excel, CSV, PDF)
 */
public class SupplierExporter {

    /**
     * Exports supplier data to Excel format
     *
     * @param suppliers List of suppliers to export
     * @param filePath Path where the Excel file will be saved
     * @throws IOException If file operations fail
     */
    public static void exportToExcel(List<Supplier> suppliers, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Suppliers");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Email", "Phone", "Category", "Status", "Reliability"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (Supplier supplier : suppliers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(supplier.getSupplierId());
                row.createCell(1).setCellValue(supplier.getSupplierName());
                row.createCell(2).setCellValue(supplier.getEmailAddress());
                row.createCell(3).setCellValue(supplier.getPhoneNumber());
                row.createCell(4).setCellValue(supplier.getCategory());
                row.createCell(5).setCellValue(supplier.getStatus());
                row.createCell(6).setCellValue(supplier.getReliabilityRating());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ensure directory exists
            File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    /**
     * Exports supplier data to CSV format
     *
     * @param suppliers List of suppliers to export
     * @param filePath Path where the CSV file will be saved
     * @throws IOException If file operations fail
     */
    public static void exportToCSV(List<Supplier> suppliers, String filePath) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID,Name,Email,Phone,Category,Status,Reliability\n");

        for (Supplier supplier : suppliers) {
            // Escape values with quotes to handle commas in fields
            csvContent.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d\n",
                    supplier.getSupplierId(),
                    escapeCSV(supplier.getSupplierName()),
                    escapeCSV(supplier.getEmailAddress()),
                    escapeCSV(supplier.getPhoneNumber()),
                    escapeCSV(supplier.getCategory()),
                    escapeCSV(supplier.getStatus()),
                    supplier.getReliabilityRating()));
        }

        // Ensure directory exists
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        Files.writeString(Path.of(filePath), csvContent.toString());
    }

    /**
     * Escapes CSV field values that contain quotes or commas
     *
     * @param value The value to escape
     * @return Escaped CSV field value
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // Replace quotes with double quotes (CSV standard)
        return value.replace("\"", "\"\"");
    }

    /**
     * Exports supplier data to PDF format
     *
     * @param suppliers List of suppliers to export
     * @param filePath Path where the PDF file will be saved
     */
    public static void exportToPDF(List<Supplier> suppliers, String filePath) {
        // PDF implementation would use a library such as iText or PDFBox
        // This is a placeholder that notifies the user about this functionality
        AlertHelper.showInformationDialog("PDF Export",
                "PDF export requires additional libraries. Please add iText or PDFBox to your project.");

        // Implementation example with iText would look something like this:
        // try {
        //     Document document = new Document();
        //     PdfWriter.getInstance(document, new FileOutputStream(filePath));
        //     document.open();
        //
        //     PdfPTable table = new PdfPTable(7);
        //     // Add table headers
        //     String[] headers = {"ID", "Name", "Email", "Phone", "Category", "Status", "Reliability"};
        //     for (String header : headers) {
        //         table.addCell(header);
        //     }
        //
        //     // Add supplier data
        //     for (Supplier supplier : suppliers) {
        //         table.addCell(supplier.getSupplierId());
        //         table.addCell(supplier.getSupplierName());
        //         table.addCell(supplier.getEmailAddress());
        //         table.addCell(supplier.getPhoneNumber());
        //         table.addCell(supplier.getCategory());
        //         table.addCell(supplier.getStatus());
        //         table.addCell(String.valueOf(supplier.getReliabilityRating()));
        //     }
        //
        //     document.add(table);
        //     document.close();
        // } catch (Exception e) {
        //     AlertHelper.showErrorDialog("Export Error",
        //         "Failed to export to PDF: " + e.getMessage());
        // }
    }
}