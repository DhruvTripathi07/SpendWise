package com.spendwise.backend.service;

import com.spendwise.backend.dto.BudgetRequest;
import com.spendwise.backend.exception.ResourceNotFoundException;
import com.spendwise.backend.model.Budget;
import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.BudgetRepository;
import com.spendwise.backend.dto.BudgetResponse;
import com.spendwise.backend.repository.UserRepository;

import com.spendwise.backend.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            ExpenseRepository expenseRepository) {

        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    // CREATE
    public Budget createBudget(BudgetRequest request) {

        User currentUser = getCurrentUser();

        // Prevent duplicate budget for the same category and month
        if (budgetRepository.findByUserIdAndCategoryAndMonth(
                currentUser.getId(),
                request.getCategory(),
                request.getMonth()).isPresent()) {

            throw new IllegalArgumentException(
                    "A budget already exists for this category and month");
        }

        Budget budget = new Budget();

        budget.setAmount(request.getAmount());
        budget.setCategory(request.getCategory());
        budget.setMonth(request.getMonth());
        budget.setUser(currentUser);

        return budgetRepository.save(budget);
    }

    // READ ALL
    public List<BudgetResponse> getAllBudgets() {

        User currentUser = getCurrentUser();

        return budgetRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::buildBudgetResponse)
                .toList();
    }

    // READ BY ID
    public BudgetResponse getBudgetById(int id) {

        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (budget.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Budget not found");
        }

        return buildBudgetResponse(budget);
    }

    // UPDATE
    public Budget updateBudget(int id, BudgetRequest updatedBudget) {

        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (budget.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Budget not found");
        }

        // Check if the new category/month combination
        // already belongs to another budget
        budgetRepository.findByUserIdAndCategoryAndMonth(
                currentUser.getId(),
                updatedBudget.getCategory(),
                updatedBudget.getMonth()).ifPresent(existingBudget -> {

                    if (existingBudget.getId() != id) {
                        throw new IllegalArgumentException(
                                "A budget already exists for this category and month");
                    }
                });

        budget.setAmount(updatedBudget.getAmount());
        budget.setCategory(updatedBudget.getCategory());
        budget.setMonth(updatedBudget.getMonth());

        return budgetRepository.save(budget);
    }

    // DELETE
    public void deleteBudget(int id) {

        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (budget.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Budget not found");
        }

        budgetRepository.delete(budget);
    }

    public double getSpentAmount(Budget budget) {

        LocalDate startDate = budget.getMonth();
        LocalDate endDate = startDate.plusMonths(1);

        return expenseRepository.getTotalSpent(
                budget.getUser().getId(),
                budget.getCategory(),
                startDate,
                endDate);
    }
    private BudgetResponse buildBudgetResponse(Budget budget) {

        double spent = getSpentAmount(budget);

        double remaining = budget.getAmount() - spent;

        double progress = 0;

        if (budget.getAmount() > 0) {
            progress = (spent / budget.getAmount()) * 100;
        }

        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getCategory(),
                budget.getMonth(),
                spent,
                remaining,
                progress
        );
    }

    // GET CURRENT USER
    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}