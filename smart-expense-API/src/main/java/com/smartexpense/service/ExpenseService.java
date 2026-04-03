package com.smartexpense.service;

import com.smartexpense.dto.ExpenseDTO;
import com.smartexpense.entity.Expense;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByUserPaged(Long userId, Pageable pageable) {
        verifyOwnership(userId);
        return expenseRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    // ✅ Ownership verification helper
    private void verifyOwnership(Long resourceUserId) {
        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!loggedInUser.getId().equals(resourceUserId)) {
            throw new AccessDeniedException("You can only access your own data.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + dto.getUserId()));
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

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByUser(Long userId) {
        verifyOwnership(userId);                                    // ← ownership check
        return expenseRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found: " + id));
        verifyOwnership(expense.getUser().getId());                 // ← ownership check
        return toDTO(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> filterByCategory(Long userId, String category) {
        verifyOwnership(userId);                                    // ← ownership check
        return expenseRepository.findByUserIdAndCategory(userId, category.toUpperCase())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> filterByDateRange(Long userId, LocalDate from, LocalDate to) {
        verifyOwnership(userId);                                    // ← ownership check
        return expenseRepository.findByUserIdAndDateBetween(userId, from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> filterByCategoryAndDate(Long userId, String category,
                                                     LocalDate from, LocalDate to) {
        verifyOwnership(userId);                                    // ← ownership check
        return expenseRepository
                .findByUserIdAndCategoryAndDateBetween(
                        userId, category.toUpperCase(), from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found: " + id));
        verifyOwnership(expense.getUser().getId());                 // ← ownership check
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory().toUpperCase());
        expense.setDescription(dto.getDescription());
        expense.setDate(dto.getDate());
        return toDTO(expenseRepository.save(expense));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found: " + id));
        verifyOwnership(expense.getUser().getId());                 // ← ownership check
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