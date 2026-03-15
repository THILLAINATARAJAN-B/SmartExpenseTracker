package com.smartexpense.service;

import com.smartexpense.dto.BudgetDTO;
import com.smartexpense.dto.DashboardDTO;
import com.smartexpense.entity.Budget;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.BudgetRepository;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User mockUser;
    private Budget mockBudget;
    private BudgetDTO mockBudgetDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L).name("Thill")
                .email("thill@gmail.com").password("hashed")
                .build();

        mockBudget = Budget.builder()
                .id(1L).category("FOOD")
                .monthlyLimit(new BigDecimal("3000.00"))
                .month(3).year(2026).user(mockUser)
                .build();

        mockBudgetDTO = BudgetDTO.builder()
                .category("FOOD")
                .monthlyLimit(new BigDecimal("3000.00"))
                .month(3).year(2026).userId(1L)
                .build();
    }

    @Test
    void setBudget_CreatesNew_WhenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                1L, "FOOD", 3, 2026)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        BudgetDTO result = budgetService.setBudget(mockBudgetDTO);

        assertNotNull(result);
        assertEquals("FOOD", result.getCategory());
        assertEquals(new BigDecimal("3000.00"), result.getMonthlyLimit());
    }

    @Test
    void setBudget_UpdatesExisting_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                1L, "FOOD", 3, 2026)).thenReturn(Optional.of(mockBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        BudgetDTO result = budgetService.setBudget(mockBudgetDTO);

        assertNotNull(result);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void getDashboard_ShowsOkAlert_WhenUnderBudget() {
        // ✅ Fix: use ArrayList<Object[]> with explicit type — avoids inference issue
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(new Object[]{"FOOD", new BigDecimal("200.00")});

        when(expenseRepository.findTotalSpentForMonth(1L, 3, 2026))
                .thenReturn(new BigDecimal("200.00"));
        when(expenseRepository.findSpentByCategoryForMonth(1L, 3, 2026))
                .thenReturn(categoryData);
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 3, 2026))
                .thenReturn(List.of(mockBudget));

        DashboardDTO result = budgetService.getDashboard(1L, 3, 2026);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getTotalSpentThisMonth());
        assertTrue(result.getBudgetAlerts().get("FOOD").contains("✅ OK"));
    }

    @Test
    void getDashboard_ShowsExceededAlert_WhenOverBudget() {
        // ✅ Fix: use ArrayList<Object[]> with explicit type
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(new Object[]{"FOOD", new BigDecimal("3500.00")});

        when(expenseRepository.findTotalSpentForMonth(1L, 3, 2026))
                .thenReturn(new BigDecimal("3500.00"));
        when(expenseRepository.findSpentByCategoryForMonth(1L, 3, 2026))
                .thenReturn(categoryData);
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 3, 2026))
                .thenReturn(List.of(mockBudget));

        DashboardDTO result = budgetService.getDashboard(1L, 3, 2026);

        assertNotNull(result);
        assertTrue(result.getBudgetAlerts().get("FOOD").contains("⚠ BUDGET EXCEEDED"));
    }

    @Test
    void deleteBudget_Success() {
        when(budgetRepository.existsById(1L)).thenReturn(true);
        doNothing().when(budgetRepository).deleteById(1L);

        assertDoesNotThrow(() -> budgetService.deleteBudget(1L));
        verify(budgetRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBudget_NotFound_ThrowsException() {
        when(budgetRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> budgetService.deleteBudget(99L));
    }
}
