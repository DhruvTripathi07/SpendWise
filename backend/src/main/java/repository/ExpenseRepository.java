package com.spendwise.backend.repository;

import com.spendwise.backend.model.Expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserId(int userId);

     @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.category = :category
              AND e.date >= :startDate
              AND e.date < :endDate
            """)
    double getTotalSpent(
            @Param("userId") int userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}