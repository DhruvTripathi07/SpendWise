package com.spendwise.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import com.spendwise.backend.model.Expense;
import com.spendwise.backend.service.ExpenseService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public Expense getExpense() {
        return expenseService.createDummyExpense();
}
}