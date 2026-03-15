package com.smartexpense.service;

import com.smartexpense.entity.Expense;
import com.smartexpense.repository.ExpenseRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class CsvExportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Export ALL expenses for a user
    public byte[] exportAllExpenses(Long userId) throws IOException {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return generateCsv(expenses);
    }

    // Export expenses filtered by date range
    public byte[] exportByDateRange(Long userId, LocalDate from, LocalDate to) throws IOException {
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, from, to);
        return generateCsv(expenses);
    }

    // Export expenses filtered by category
    public byte[] exportByCategory(Long userId, String category) throws IOException {
        List<Expense> expenses = expenseRepository.findByUserIdAndCategory(
                userId, category.toUpperCase());
        return generateCsv(expenses);
    }

    // Core CSV generation logic
    private byte[] generateCsv(List<Expense> expenses) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Title", "Amount (INR)", "Category", "Description", "Date", "User ID")
                .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Expense e : expenses) {
                printer.printRecord(
                        e.getId(),
                        e.getTitle(),
                        e.getAmount(),
                        e.getCategory(),
                        e.getDescription() != null ? e.getDescription() : "",
                        e.getDate(),
                        e.getUser().getId()
                );
            }
        }

        return out.toByteArray();
    }
}
