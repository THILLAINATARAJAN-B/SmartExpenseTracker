package com.smartexpense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartexpense.dto.ExpenseDTO;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.security.JwtUtil;
import com.smartexpense.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    private ExpenseDTO mockExpenseDTO;

    @BeforeEach
    void setUp() {
        mockExpenseDTO = ExpenseDTO.builder()
                .id(1L)
                .title("Lunch")                          // ← ADD THIS
                .amount(new BigDecimal("500.00"))
                .category("FOOD")
                .description("Lunch")
                .date(LocalDate.of(2026, 3, 15))
                .userId(1L)
                .build();
    }


    @Test
    @WithMockUser(username = "thill@gmail.com", roles = "USER")
    void getExpensesByUser_ReturnsListOf1() throws Exception {
        when(expenseService.getExpensesByUser(1L)).thenReturn(List.of(mockExpenseDTO));

        mockMvc.perform(get("/api/expenses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("FOOD"));
    }

    @Test
    @WithMockUser(username = "thill@gmail.com", roles = "USER")
    void addExpense_Returns201() throws Exception {
        when(expenseService.addExpense(any(ExpenseDTO.class))).thenReturn(mockExpenseDTO);

        mockMvc.perform(post("/api/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockExpenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    @WithMockUser(username = "thill@gmail.com", roles = "USER")
    void deleteExpense_Returns200() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
