package com.spendwise.backend.controller;

import com.spendwise.backend.dto.BudgetRequest;
import com.spendwise.backend.dto.BudgetResponse;
import com.spendwise.backend.service.BudgetService;
import com.spendwise.backend.model.Budget;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Budget createBudget(@Valid @RequestBody BudgetRequest request) {
        return budgetService.createBudget(request);
    }

    // READ ALL
    @GetMapping
    public List<BudgetResponse> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public BudgetResponse getBudgetById(@PathVariable int id) {
        return budgetService.getBudgetById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable int id,
            @Valid @RequestBody BudgetRequest request) {

        return budgetService.updateBudget(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@PathVariable int id) {
        budgetService.deleteBudget(id);
    }
}