package com.spendwise.backend.service;

import com.spendwise.backend.exception.ExpenseNotFoundException;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service; 
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.model.Expense; 
import com.spendwise.backend.dto.ExpenseRequest;


@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    //CREATE, READ, UPDATE, DELETE (CRUD) operations for Expense entity
    //CREATE
    // CREATE
    public Expense createExpense(ExpenseRequest request) {

        Expense expense = new Expense();

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setPaymentMode(request.getPaymentMode());
        expense.setDate(request.getDate());

        return expenseRepository.save(expense);
    }
    //READALL
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    //READBYID
    public Expense getExpenseById(int id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with ID" + id + " not found"));
    }
    //UPDATE
    public Expense updateExpense(int id, Expense expense) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(
                    "Expense with ID " + id + " not found");
        }
        expense.setId(id);
        return expenseRepository.save(expense);
    }
    //DELETE
    public void deleteExpense(int id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(
                    "Expense with ID " + id + " not found");
        }
        expenseRepository.deleteById(id);
    }
}