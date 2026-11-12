package com.spendwise.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import com.spendwise.backend.service.ExpenseService;
import com.spendwise.backend.dto.ExpenseRequest;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;
import com.spendwise.backend.dto.MonthlySpendingResponse;

import jakarta.validation.Valid;

import java.time.YearMonth;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;


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
    public Page<ExpenseResponse> getAllExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) YearMonth month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (page < 0 || size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page must be non-negative and size must be positive");
        }

        return expenseService.getExpenses(category, month, page, Math.min(size, 100));
    }

    @GetMapping("/categories")
    public List<String> getExpenseCategories(
            @RequestParam(required = false) YearMonth month) {
        return expenseService.getExpenseCategories(month);
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

    @GetMapping("/monthly-spending")
    public List<MonthlySpendingResponse> getMonthlySpending(
            @RequestParam YearMonth month) {

        return expenseService.getMonthlySpending(month);
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
