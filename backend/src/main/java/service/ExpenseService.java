package com.spendwise.backend.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service; 
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.model.Expense; 

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createDummyExpense() {

        Expense expense = new Expense();

        expense.setId(1);
        expense.setAmount(350);
        expense.setCategory("Food");
        expense.setDescription("Pizza");
        expense.setPaymentMode("UPI");
        expense.setDate(LocalDate.now());

        Expense savedExpense = expenseRepository.save(expense);
        return savedExpense;
}
}