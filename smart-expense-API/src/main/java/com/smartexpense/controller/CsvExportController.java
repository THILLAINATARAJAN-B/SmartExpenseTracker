package com.smartexpense.controller;

import com.smartexpense.service.CsvExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@Tag(name = "CSV Export", description = "Download expenses as CSV file")
public class CsvExportController {

    @Autowired
    private CsvExportService csvExportService;

    @GetMapping("/expenses/{userId}")
    @Operation(summary = "Export all expenses for a user as CSV")
    public ResponseEntity<byte[]> exportAll(@PathVariable Long userId) throws IOException {
        byte[] csvData = csvExportService.exportAllExpenses(userId);
        return buildCsvResponse(csvData, "expenses_user_" + userId + ".csv");
    }

    @GetMapping("/expenses/{userId}/by-date")
    @Operation(summary = "Export expenses filtered by date range as CSV")
    public ResponseEntity<byte[]> exportByDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
            throws IOException {
        byte[] csvData = csvExportService.exportByDateRange(userId, from, to);
        return buildCsvResponse(csvData, "expenses_" + from + "_to_" + to + ".csv");
    }

    @GetMapping("/expenses/{userId}/by-category")
    @Operation(summary = "Export expenses filtered by category as CSV")
    public ResponseEntity<byte[]> exportByCategory(
            @PathVariable Long userId,
            @RequestParam String category) throws IOException {
        byte[] csvData = csvExportService.exportByCategory(userId, category);
        return buildCsvResponse(csvData, "expenses_" + category.toLowerCase() + ".csv");
    }

    // Helper — builds the downloadable CSV HTTP response
    private ResponseEntity<byte[]> buildCsvResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(data.length)
                .body(data);
    }
}
