package com.spendwise.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import com.spendwise.backend.service.ExpenseService;
import com.spendwise.backend.dto.ExpenseRequest;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest request) {
        return expenseService.createExpense(request);
    }
    
    @GetMapping
    public List<ExpenseResponse> getAllExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) YearMonth month) {

        if (category != null && !category.isBlank() && month != null) {
            return expenseService.getExpensesByCategoryAndMonth(category, month.atDay(1));
        }

        if (category != null && !category.isBlank()) {
            return expenseService.getExpensesByCategory(category);
        }

        if (month != null) {
            return expenseService.getExpensesByMonth(month.atDay(1));
        }

        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(@PathVariable int id) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/summary")
    public MonthlySummaryResponse getMonthlySummary(
            @RequestParam YearMonth month) {

        return expenseService.getMonthlySummary(month);
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable int id,
            @Valid @RequestBody ExpenseRequest updatedExpense) {
        return expenseService.updateExpense(id, updatedExpense);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable int id) {
        expenseService.deleteExpense(id);
    }

}