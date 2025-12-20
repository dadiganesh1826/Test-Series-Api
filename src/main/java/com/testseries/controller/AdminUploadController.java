package com.testseries.controller;

import com.testseries.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminUploadController {

    private final ExcelUploadService excelUploadService;

    @PostMapping("/questions/{testSeriesId}")
    public ResponseEntity<?> uploadQuestions(@PathVariable Long testSeriesId, @RequestParam("file") MultipartFile file) {
        try {
            int count = excelUploadService.uploadQuestions(testSeriesId, file);
            return ResponseEntity.ok(Map.of("message", "Successfully uploaded " + count + " questions"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Questions Template");

        // Header Row
        Row header = sheet.createRow(0);
        String[] columns = {
            "Question Text (Required)", 
            "Option A (Required)", 
            "Option B (Required)", 
            "Option C (Required)", 
            "Option D (Required)", 
            "Correct Option (A/B/C/D)", 
            "Explanation", 
            "Marks (Default: 1)", 
            "Negative Marks (Default: 0)"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 6000); // Set width
        }

        // Add a sample row
        Row sample = sheet.createRow(1);
        sample.createCell(0).setCellValue("What is the capital of France?");
        sample.createCell(1).setCellValue("Berlin");
        sample.createCell(2).setCellValue("Madrid");
        sample.createCell(3).setCellValue("Paris");
        sample.createCell(4).setCellValue("Rome");
        sample.createCell(5).setCellValue("C");
        sample.createCell(6).setCellValue("Paris is the capital and most populous city of France.");
        sample.createCell(7).setCellValue(1);
        sample.createCell(8).setCellValue(0); // 0 negative marks

        workbook.write(stream);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(stream.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=questions_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
