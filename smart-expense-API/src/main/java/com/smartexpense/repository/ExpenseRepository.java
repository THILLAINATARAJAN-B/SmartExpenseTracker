package com.smartexpense.repository;

import com.smartexpense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // All expenses for a user
    List<Expense> findByUserId(Long userId);

    // All expenses for a user — paginated
    Page<Expense> findByUserId(Long userId, Pageable pageable);    // ← FIXED

    // Filter by category
    List<Expense> findByUserIdAndCategory(Long userId, String category);

    // Filter by date range
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // Filter by category AND date range
    List<Expense> findByUserIdAndCategoryAndDateBetween(
            Long userId, String category, LocalDate startDate, LocalDate endDate);

    // Spent per category this month
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND MONTH(e.date) = :month " +
           "AND YEAR(e.date) = :year " +
           "GROUP BY e.category")
    List<Object[]> findSpentByCategoryForMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Total spent this month
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND MONTH(e.date) = :month " +
           "AND YEAR(e.date) = :year")
    BigDecimal findTotalSpentForMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);
}