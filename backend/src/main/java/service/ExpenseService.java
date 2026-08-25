package com.spendwise.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service; 
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.model.Expense; 

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    //CREATE, READ, UPDATE, DELETE (CRUD) operations for Expense entity
    //CREATE
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    //READALL
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    //READBYID
    public Expense getExpenseById(int id) {
        return expenseRepository.findById(id)
                .orElse(null);
    }
    //UPDATE
    public Expense updateExpense(int id, Expense expense) {
        expense.setId(id);
        return expenseRepository.save(expense);
    }
    //DELETE
    public void deleteExpense(int id) {
        expenseRepository.deleteById(id);
    }
}