package com.extreme;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceExcel {

    // Organized folder structure for the "Archived Logs" feature
    private static final String SAVE_PATH = "attendance-records/";

    public void export(String sessionName, List<String> studentData) {
        // Create directory if it doesn't exist
        File directory = new File(SAVE_PATH);
        if (!directory.exists()) directory.mkdirs();

        // Dynamic naming: Attendance_BCA-6A_2026-04-15.xlsx
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String fileName = SAVE_PATH + "Attendance_" + sessionName + "_" + timestamp + ".xlsx";

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Attendance Log");

            // --- HEADER STYLING ---
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Timestamp", "Student Name / ID", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- DATA INJECTION ---
            int rowIdx = 1;
            for (String record : studentData) {
                Row row = sheet.createRow(rowIdx++);
                // Assuming record is currently a simple string;
                // splitting logic can be added here if you pass objects
                row.createCell(1).setCellValue(record);
                row.createCell(2).setCellValue("PRESENT");
            }

            // Auto-size columns for a "premium" feel
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write File
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                wb.write(fileOut);
                System.out.println("✅ Report Generated: " + fileName);
            }

        } catch (Exception e) {
            System.err.println("❌ Excel Export Failed: " + e.getMessage());
        }
    }
}