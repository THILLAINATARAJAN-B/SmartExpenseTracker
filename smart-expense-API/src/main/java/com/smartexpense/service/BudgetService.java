package com.smartexpense.service;

import com.smartexpense.dto.BudgetDTO;
import com.smartexpense.dto.DashboardDTO;
import com.smartexpense.entity.Budget;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.BudgetRepository;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional(rollbackFor = Exception.class)
    public BudgetDTO setBudget(BudgetDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + dto.getUserId()));

        Budget budget = budgetRepository
                .findByUserIdAndCategoryAndMonthAndYear(
                        dto.getUserId(), dto.getCategory().toUpperCase(),
                        dto.getMonth(), dto.getYear())
                .orElse(new Budget());

        budget.setCategory(dto.getCategory().toUpperCase());
        budget.setMonthlyLimit(dto.getMonthlyLimit());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setUser(user);

        return toDTO(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetDTO> getBudgets(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget not found: " + id);
        }
        budgetRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(Long userId, int month, int year) {
        BigDecimal total = expenseRepository.findTotalSpentForMonth(userId, month, year);

        List<Object[]> spentRaw = expenseRepository
                .findSpentByCategoryForMonth(userId, month, year);
        Map<String, BigDecimal> spentByCategory = new HashMap<>();
        for (Object[] row : spentRaw) {
            spentByCategory.put((String) row[0], (BigDecimal) row[1]);
        }

        List<Budget> budgets = budgetRepository
                .findByUserIdAndMonthAndYear(userId, month, year);
        Map<String, BigDecimal> budgetByCategory = new HashMap<>();
        for (Budget b : budgets) {
            budgetByCategory.put(b.getCategory(), b.getMonthlyLimit());
        }

        Map<String, String> alerts = new HashMap<>();
        for (Budget b : budgets) {
            BigDecimal spent = spentByCategory.getOrDefault(
                    b.getCategory(), BigDecimal.ZERO);
            if (spent.compareTo(b.getMonthlyLimit()) > 0) {
                alerts.put(b.getCategory(), "⚠ BUDGET EXCEEDED! Spent: ₹"
                        + spent + " | Limit: ₹" + b.getMonthlyLimit());
            } else {
                alerts.put(b.getCategory(), "✅ OK. Spent: ₹"
                        + spent + " | Limit: ₹" + b.getMonthlyLimit());
            }
        }

        return DashboardDTO.builder()
                .totalSpentThisMonth(total)
                .spentByCategory(spentByCategory)
                .budgetByCategory(budgetByCategory)
                .budgetAlerts(alerts)
                .build();
    }

    private BudgetDTO toDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .monthlyLimit(budget.getMonthlyLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .userId(budget.getUser().getId())
                .build();
    }
}