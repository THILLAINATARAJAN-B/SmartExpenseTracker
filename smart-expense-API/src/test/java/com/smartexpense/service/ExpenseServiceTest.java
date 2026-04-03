package com.smartexpense.service;

import com.smartexpense.dto.ExpenseDTO;
import com.smartexpense.entity.Expense;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.ExpenseRepository;
import com.smartexpense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)  // ← ADD THIS
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ExpenseService expenseService;

    private User mockUser;
    private Expense mockExpense;
    private ExpenseDTO mockExpenseDTO;

    @BeforeEach
    void setUp() {
        // ✅ Mock SecurityContext so verifyOwnership() doesn't NPE
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("thill@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        mockUser = User.builder()
                .id(1L)
                .name("Thill")
                .email("thill@gmail.com")
                .password("hashed")
                .build();

        mockExpense = Expense.builder()
                .id(1L)
                .title("Lunch")
                .amount(new BigDecimal("350.00"))
                .category("FOOD")
                .description("South Indian")
                .date(LocalDate.of(2026, 3, 15))
                .user(mockUser)
                .build();

        mockExpenseDTO = ExpenseDTO.builder()
                .title("Lunch")
                .amount(new BigDecimal("350.00"))
                .category("FOOD")
                .description("South Indian")
                .date(LocalDate.of(2026, 3, 15))
                .userId(1L)
                .build();
    }

    @Test
    void addExpense_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);

        ExpenseDTO result = expenseService.addExpense(mockExpenseDTO);

        assertNotNull(result);
        assertEquals("Lunch", result.getTitle());
        assertEquals(new BigDecimal("350.00"), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void addExpense_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        mockExpenseDTO.setUserId(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.addExpense(mockExpenseDTO));
    }

    @Test
    void getExpensesByUser_ReturnsList() {
        // ✅ Mock userRepository so verifyOwnership() can look up the user
        when(userRepository.findByEmail("thill@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(mockExpense));

        List<ExpenseDTO> result = expenseService.getExpensesByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getTitle());
    }

    @Test
    void getExpenseById_Success() {
        // ✅ Mock userRepository so verifyOwnership() can look up the user
        when(userRepository.findByEmail("thill@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

        ExpenseDTO result = expenseService.getExpenseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getExpenseById_NotFound_ThrowsException() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.getExpenseById(99L));
    }

    @Test
    void deleteExpense_Success() {
        // ✅ Fix: use findById instead of existsById to match service logic
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));
        when(userRepository.findByEmail("thill@gmail.com")).thenReturn(Optional.of(mockUser));
        doNothing().when(expenseRepository).deleteById(1L);

        assertDoesNotThrow(() -> expenseService.deleteExpense(1L));
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateExpense_Success() {
        when(userRepository.findByEmail("thill@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);

        ExpenseDTO result = expenseService.updateExpense(1L, mockExpenseDTO);

        assertNotNull(result);
        assertEquals("Lunch", result.getTitle());
    }
}