package com.smartexpense.service;

import com.smartexpense.dto.ExpenseDTO;
import com.smartexpense.entity.Expense;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // Add expense
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));
        Expense expense = Expense.builder()
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .category(dto.getCategory().toUpperCase())
                .description(dto.getDescription())
                .date(dto.getDate())
                .user(user)
                .build();
        return toDTO(expenseRepository.save(expense));
    }

    // Get all expenses for a user
    public List<ExpenseDTO> getExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Get expense by ID
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        return toDTO(expense);
    }

    // Filter expenses by category
    public List<ExpenseDTO> filterByCategory(Long userId, String category) {
        return expenseRepository.findByUserIdAndCategory(userId, category.toUpperCase())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Filter expenses by date range
    public List<ExpenseDTO> filterByDateRange(Long userId, LocalDate from, LocalDate to) {
        return expenseRepository.findByUserIdAndDateBetween(userId, from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Filter by category AND date range
    public List<ExpenseDTO> filterByCategoryAndDate(Long userId, String category,
                                                     LocalDate from, LocalDate to) {
        return expenseRepository
                .findByUserIdAndCategoryAndDateBetween(userId, category.toUpperCase(), from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Update expense
    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory().toUpperCase());
        expense.setDescription(dto.getDescription());
        expense.setDate(dto.getDate());
        return toDTO(expenseRepository.save(expense));
    }

    // Delete expense
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .date(expense.getDate())
                .userId(expense.getUser().getId())
                .build();
    }
}
