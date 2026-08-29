package com.spendwise.backend.repository;

import com.spendwise.backend.model.Expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public interface ExpenseRepository
        extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserId(int userId);

    List<Expense> findByUserIdAndCategory(
      int userId, 
      String category);
    
    List<Expense> findByUserIdAndDateGreaterThanEqualAndDateLessThan(
      int userId,
      LocalDate startDate, 
      LocalDate endDate);

    List<Expense> findByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
        int userId,
        String category,
        LocalDate startDate,
        LocalDate endDate);
        
     @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.category = :category
              AND e.date >= :startDate
              AND e.date < :endDate
            """)
    BigDecimal getTotalSpent(
            @Param("userId") int userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                    SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total
                    FROM Expense e
                    WHERE e.user.id = :userId
                      AND e.date >= :startDate
                      AND e.date < :endDate
                    GROUP BY e.category
                    """)
    List<Map<String, Object>> getCategoryTotals(
                    @Param("userId") int userId,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
}