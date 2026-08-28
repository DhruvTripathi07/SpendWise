package com.spendwise.backend.service;

import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.UserRepository;
import com.spendwise.backend.exception.ResourceNotFoundException;
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.model.Expense;
import com.spendwise.backend.dto.ExpenseRequest;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service; 
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    
    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
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

        expense.setUser(getCurrentUser());

        return expenseRepository.save(expense);
    }
    
    //READALL
    public List<ExpenseResponse> getAllExpenses() {
        User user = getCurrentUser();

        return expenseRepository.findByUserId(user.getId())
                .stream()
                .map(this::buildExpenseResponse)
                .toList();
    }
    //READBYID
    public ExpenseResponse getExpenseById(int id) {
        User user = getCurrentUser();

        Expense expense = expenseRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (expense.getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException("Expense not found");
        }

        return buildExpenseResponse(expense);
    }

    //UPDATE
    public Expense updateExpense(int id, ExpenseRequest updatedExpense) {

        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (expense.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setDescription(updatedExpense.getDescription());
        expense.setPaymentMode(updatedExpense.getPaymentMode());
        expense.setDate(updatedExpense.getDate());

        return expenseRepository.save(expense);
    }

    //DELETE
    public void deleteExpense(int id) {
        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (expense.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponse buildExpenseResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getPaymentMode(),
                expense.getDate());
    }

    public MonthlySummaryResponse getMonthlySummary(LocalDate month) {

        User currentUser = getCurrentUser();

        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        List<Map<String, Object>> categoryTotals = expenseRepository.getCategoryTotals(
                currentUser.getId(),
                startDate,
                endDate);

        Map<String, Double> categories = new HashMap<>();
        double totalSpent = 0;

        for (Map<String, Object> row : categoryTotals) {

            String category = (String) row.get("category");
            double total = ((Number) row.get("total")).doubleValue();

            categories.put(category, total);
            totalSpent += total;
        }

        return new MonthlySummaryResponse(
                startDate,
                totalSpent,
                categories);
    }
    
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