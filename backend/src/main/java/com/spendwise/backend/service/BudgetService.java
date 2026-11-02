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
import java.time.YearMonth;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public BudgetResponse createBudget(BudgetRequest request) {

        User currentUser = getCurrentUser();
        LocalDate normalizedMonth = normalizeMonth(request.getMonth());

        // Prevent duplicate budget for the same category and month
        if (budgetRepository.findByUserIdAndCategoryAndMonth(
                currentUser.getId(),
                request.getCategory(),
                normalizedMonth).isPresent()) {

            throw new IllegalArgumentException(
                    "A budget already exists for this category and month");
        }

        Budget budget = new Budget();

        budget.setAmount(request.getAmount());
        budget.setCategory(request.getCategory());
        budget.setMonth(normalizedMonth);
        budget.setUser(currentUser);

        Budget savedBudget = budgetRepository.save(budget);

        return buildBudgetResponse(savedBudget);
    }

    // READ ALL
    public List<BudgetResponse> getAllBudgets() {

        User currentUser = getCurrentUser();
        List<Budget> budgets = budgetRepository.findByUserId(currentUser.getId());

        if (budgets.isEmpty()) {
            return List.of();
        }

        LocalDate startDate = budgets.stream()
                .map(Budget::getMonth)
                .map(month -> YearMonth.from(month).atDay(1))
                .min(LocalDate::compareTo)
                .orElseThrow();
        LocalDate endDate = budgets.stream()
                .map(Budget::getMonth)
                .map(month -> YearMonth.from(month).plusMonths(1).atDay(1))
                .max(LocalDate::compareTo)
                .orElseThrow();

        Map<BudgetSpendingKey, BigDecimal> spentByBudget =
                getSpentAmountsByBudget(currentUser.getId(), startDate, endDate);

        return budgets.stream()
                .map(budget -> buildBudgetResponse(
                        budget,
                        spentByBudget.getOrDefault(
                                new BudgetSpendingKey(
                                        YearMonth.from(budget.getMonth()),
                                        budget.getCategory()),
                                BigDecimal.ZERO)))
                .toList();
    }

    public List<BudgetResponse> getBudgetsByMonth(YearMonth month) {

        User currentUser = getCurrentUser();
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        List<Budget> budgets = budgetRepository
                .findByUserIdAndMonthGreaterThanEqualAndMonthLessThan(
                        currentUser.getId(),
                        startDate,
                        endDate);
        if (budgets.isEmpty()) {
            return List.of();
        }

        Map<String, BigDecimal> spentByCategory = new HashMap<>();
        for (Map<String, Object> row : expenseRepository.getCategoryTotals(
                currentUser.getId(),
                startDate,
                endDate)) {
            spentByCategory.put((String) row.get("category"), (BigDecimal) row.get("total"));
        }

        return budgets.stream()
                .map(budget -> buildBudgetResponse(
                        budget,
                        spentByCategory.getOrDefault(budget.getCategory(), BigDecimal.ZERO)))
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
    public BudgetResponse updateBudget(int id, BudgetRequest updatedBudget) {

        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (budget.getUser().getId() != currentUser.getId()) {
            throw new ResourceNotFoundException("Budget not found");
        }

        LocalDate normalizedMonth = normalizeMonth(updatedBudget.getMonth());

        // Check if the new category/month combination
        // already belongs to another budget
        budgetRepository.findByUserIdAndCategoryAndMonth(
                currentUser.getId(),
                updatedBudget.getCategory(),
                normalizedMonth).ifPresent(existingBudget -> {

                    if (existingBudget.getId() != id) {
                        throw new IllegalArgumentException(
                                "A budget already exists for this category and month");
                    }
                });

        budget.setAmount(updatedBudget.getAmount());
        budget.setCategory(updatedBudget.getCategory());
        budget.setMonth(normalizedMonth);

        Budget savedBudget = budgetRepository.save(budget);
        return buildBudgetResponse(savedBudget);
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

    public BigDecimal getSpentAmount(Budget budget) {

        YearMonth budgetMonth = YearMonth.from(budget.getMonth());
        LocalDate startDate = budgetMonth.atDay(1);
        LocalDate endDate = budgetMonth.plusMonths(1).atDay(1);

        return expenseRepository.getTotalSpent(
                budget.getUser().getId(),
                budget.getCategory(),
                startDate,
                endDate);
    }

    private LocalDate normalizeMonth(LocalDate month) {
        return month.withDayOfMonth(1);
    }
    
    private BudgetResponse buildBudgetResponse(Budget budget) {
        return buildBudgetResponse(budget, getSpentAmount(budget));
    }

    private BudgetResponse buildBudgetResponse(Budget budget, BigDecimal spent) {
        BigDecimal remaining = budget.getAmount().subtract(spent);

        BigDecimal progress = BigDecimal.ZERO;

        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        BigDecimal progressPercent = progress.min(BigDecimal.valueOf(100));

        boolean overBudget = spent.compareTo(budget.getAmount()) > 0;

        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getCategory(),
                YearMonth.from(budget.getMonth()),
                spent,
                remaining,
                progress,
                progressPercent,
                overBudget
        );
    }

    private Map<BudgetSpendingKey, BigDecimal> getSpentAmountsByBudget(
            int userId,
            LocalDate startDate,
            LocalDate endDate) {

        Map<BudgetSpendingKey, BigDecimal> spentByBudget = new HashMap<>();
        for (Object[] row : expenseRepository.getBudgetTotalsByMonth(
                userId,
                startDate,
                endDate)) {
            YearMonth month = YearMonth.of(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue());
            String category = (String) row[2];
            BigDecimal total = (BigDecimal) row[3];
            spentByBudget.put(new BudgetSpendingKey(month, category), total);
        }
        return spentByBudget;
    }

    private record BudgetSpendingKey(YearMonth month, String category) {
    }

    // GET CURRENT USER
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