package com.smartexpense.controller;

import com.smartexpense.dto.BudgetDTO;
import com.smartexpense.dto.DashboardDTO;
import com.smartexpense.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budget & Dashboard", description = "Set budgets, get alerts, view dashboard")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Set or update a monthly budget for a category")
    public ResponseEntity<BudgetDTO> setBudget(@Valid @RequestBody BudgetDTO dto) {
        return ResponseEntity.ok(budgetService.setBudget(dto));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all budgets for a user for a given month and year")
    public ResponseEntity<List<BudgetDTO>> getBudgets(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(budgetService.getBudgets(userId, month, year));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<String> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok("Budget deleted successfully");
    }

    @GetMapping("/dashboard/{userId}")
    @Operation(summary = "Get dashboard: total spent, category breakdown, and budget alerts")
    public ResponseEntity<DashboardDTO> getDashboard(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(budgetService.getDashboard(userId, month, year));
    }
}
