package com.spendwise.backend.repository;

import com.spendwise.backend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUserId(int userId);

    Optional<Budget> findByUserIdAndCategoryAndMonth(
            int userId,
            String category,
            LocalDate month);
}