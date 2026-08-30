package com.spendwise.backend.service;

import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.UserRepository;
import com.spendwise.backend.exception.ResourceNotFoundException;
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.model.Expense;
import com.spendwise.backend.dto.ExpenseRequest;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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
    public ExpenseResponse createExpense(ExpenseRequest request) {

        Expense expense = new Expense();

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setPaymentMode(request.getPaymentMode());
        expense.setDate(request.getDate());

        expense.setUser(getCurrentUser());

        Expense savedExpense = expenseRepository.save(expense);
        return buildExpenseResponse(savedExpense);
    }
    
    //READALL
    public List<ExpenseResponse> getAllExpenses() {
        User user = getCurrentUser();

        return expenseRepository.findByUserId(user.getId())
                .stream()
                .map(this::buildExpenseResponse)
                .toList();
    }

    public List<ExpenseResponse> getExpensesByCategory(String category) {

        User currentUser = getCurrentUser();

        return expenseRepository
                .findByUserIdAndCategory(currentUser.getId(), category)
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

    public List<ExpenseResponse> getExpensesByMonth(LocalDate month) {

        User currentUser = getCurrentUser();

        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        return expenseRepository
                .findByUserIdAndDateGreaterThanEqualAndDateLessThan(
                        currentUser.getId(),
                        startDate,
                        endDate)
                .stream()
                .map(this::buildExpenseResponse)
                .toList();
    }

    public List<ExpenseResponse> getExpensesByCategoryAndMonth(
            String category,
            LocalDate month) {

        User currentUser = getCurrentUser();

        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        return expenseRepository
                .findByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
                        currentUser.getId(),
                        category,
                        startDate,
                        endDate)
                .stream()
                .map(this::buildExpenseResponse)
                .toList();
    }

    //UPDATE
    public ExpenseResponse updateExpense(int id, ExpenseRequest updatedExpense) {

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

        Expense savedExpense = expenseRepository.save(expense);
        return buildExpenseResponse(savedExpense);
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

    public MonthlySummaryResponse getMonthlySummary(YearMonth month) {

        User currentUser = getCurrentUser();

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        List<Map<String, Object>> categoryTotals = expenseRepository.getCategoryTotals(
                currentUser.getId(),
                startDate,
                endDate);

        Map<String, BigDecimal> categories = new HashMap<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Map<String, Object> row : categoryTotals) {

            String category = (String) row.get("category");
            BigDecimal total = (BigDecimal) row.get("total");

            categories.put(category, total);
            totalSpent = totalSpent.add(total);
        }

        return new MonthlySummaryResponse(
                month,
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
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}