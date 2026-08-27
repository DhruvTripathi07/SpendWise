package com.spendwise.backend.repository;

import com.spendwise.backend.model.Expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserId(int userId);
}