package com.spendwise.backend.repository;

import com.spendwise.backend.model.Expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Expense> findAllByUserId(int userId, Pageable pageable);

    List<Expense> findByUserIdAndCategory(
      int userId, 
      String category);

    Page<Expense> findAllByUserIdAndCategory(
      int userId,
      String category,
      Pageable pageable);
    
    List<Expense> findByUserIdAndDateGreaterThanEqualAndDateLessThan(
      int userId,
      LocalDate startDate, 
      LocalDate endDate);

    Page<Expense> findAllByUserIdAndDateGreaterThanEqualAndDateLessThan(
      int userId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable);

    List<Expense> findTop5ByUserIdAndDateGreaterThanEqualAndDateLessThanOrderByDateDescIdDesc(
      int userId,
      LocalDate startDate,
      LocalDate endDate);

    List<Expense> findByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
        int userId,
        String category,
        LocalDate startDate,
        LocalDate endDate);

    Page<Expense> findAllByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
        int userId,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable);

    @Query("""
                    SELECT DISTINCT e.category
                    FROM Expense e
                    WHERE e.user.id = :userId
                    ORDER BY e.category
                    """)
    List<String> findCategoriesByUserId(@Param("userId") int userId);

    @Query("""
                    SELECT DISTINCT e.category
                    FROM Expense e
                    WHERE e.user.id = :userId
                      AND e.date >= :startDate
                      AND e.date < :endDate
                    ORDER BY e.category
                    """)
    List<String> findCategoriesByUserIdAndDateRange(
                    @Param("userId") int userId,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
        
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
                    SELECT YEAR(e.date), MONTH(e.date), e.category, COALESCE(SUM(e.amount), 0)
                    FROM Expense e
                    WHERE e.user.id = :userId
                      AND e.date >= :startDate
                      AND e.date < :endDate
                    GROUP BY YEAR(e.date), MONTH(e.date), e.category
                    """)
    List<Object[]> getBudgetTotalsByMonth(
                    @Param("userId") int userId,
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

    @Query("""
                    SELECT YEAR(e.date) AS year,
                          MONTH(e.date) AS month,
                          COALESCE(SUM(e.amount), 0) AS total
                    FROM Expense e
                    WHERE e.user.id = :userId
                      AND e.date >= :startDate
                      AND e.date < :endDate
                    GROUP BY YEAR(e.date), MONTH(e.date)
                    ORDER BY YEAR(e.date), MONTH(e.date)
                    """)
    List<Map<String, Object>> getMonthlyTotals(
                    @Param("userId") int userId,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
}