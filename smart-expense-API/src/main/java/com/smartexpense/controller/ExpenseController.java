package com.smartexpense.controller;

import com.smartexpense.dto.ExpenseDTO;
import com.smartexpense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense Management", description = "Add, update, delete, filter expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Add a new expense")
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody ExpenseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.addExpense(dto));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all expenses for a user")
    public ResponseEntity<List<ExpenseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesByUser(userId));
    }

    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get expenses with pagination")
    public ResponseEntity<Page<ExpenseDTO>> getByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "date") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return ResponseEntity.ok(expenseService.getExpensesByUserPaged(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID")
    public ResponseEntity<ExpenseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @GetMapping("/user/{userId}/filter")
    @Operation(summary = "Filter expenses by category and/or date range")
    public ResponseEntity<List<ExpenseDTO>> filter(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (category != null && from != null && to != null) {
            return ResponseEntity.ok(
                    expenseService.filterByCategoryAndDate(userId, category, from, to));
        } else if (category != null) {
            return ResponseEntity.ok(
                    expenseService.filterByCategory(userId, category));
        } else if (from != null && to != null) {
            return ResponseEntity.ok(
                    expenseService.filterByDateRange(userId, from, to));
        }
        return ResponseEntity.ok(expenseService.getExpensesByUser(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an expense")
    public ResponseEntity<ExpenseDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }
}