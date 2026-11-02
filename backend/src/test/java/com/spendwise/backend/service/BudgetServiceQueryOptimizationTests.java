package com.spendwise.backend.service;

import com.spendwise.backend.dto.BudgetResponse;
import com.spendwise.backend.model.Budget;
import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.BudgetRepository;
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BudgetServiceQueryOptimizationTests {

    private static final String USER_EMAIL = "user@example.com";
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private User currentUser;
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetRepository = mock(BudgetRepository.class);
        expenseRepository = mock(ExpenseRepository.class);
        userRepository = mock(UserRepository.class);
        currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(7);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(currentUser));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_EMAIL, "password"));
        budgetService = new BudgetService(
                budgetRepository,
                userRepository,
                expenseRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void selectedMonthLoadsOnlyMatchingBudgetsAndBatchesSpendingTotals() {
        YearMonth month = YearMonth.of(2026, 7);
        List<Budget> budgets = List.of(
                budget(1, "Food", month.atDay(1), "100.00"),
                budget(2, "Travel", month.atDay(1), "200.00"));
        when(budgetRepository.findByUserIdAndMonthGreaterThanEqualAndMonthLessThan(
                7, month.atDay(1), month.plusMonths(1).atDay(1))).thenReturn(budgets);
        when(expenseRepository.getCategoryTotals(
                7, month.atDay(1), month.plusMonths(1).atDay(1)))
                .thenReturn(List.of(
                        Map.of("category", "Food", "total", new BigDecimal("25.00")),
                        Map.of("category", "Travel", "total", new BigDecimal("50.00"))));

        List<BudgetResponse> responses = budgetService.getBudgetsByMonth(month);

        assertEquals(2, responses.size());
        assertEquals(new BigDecimal("25.00"), responses.get(0).getSpent());
        assertEquals(new BigDecimal("50.00"), responses.get(1).getSpent());
        verify(budgetRepository).findByUserIdAndMonthGreaterThanEqualAndMonthLessThan(
                7, month.atDay(1), month.plusMonths(1).atDay(1));
        verify(budgetRepository, never()).findByUserId(7);
        verify(expenseRepository).getCategoryTotals(
                7, month.atDay(1), month.plusMonths(1).atDay(1));
        verify(expenseRepository, never()).getTotalSpent(
                anyInt(), anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void allBudgetsUseOneGroupedSpendingQueryInsteadOfNPlusOneQueries() {
        List<Budget> budgets = List.of(
                budget(1, "Food", LocalDate.of(2026, 6, 1), "100.00"),
                budget(2, "Food", LocalDate.of(2026, 7, 1), "100.00"));
        when(budgetRepository.findByUserId(7)).thenReturn(budgets);
        when(expenseRepository.getBudgetTotalsByMonth(
                7, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 8, 1)))
                .thenReturn(List.of(
                        new Object[] { 2026, 6, "Food", new BigDecimal("20.00") },
                        new Object[] { 2026, 7, "Food", new BigDecimal("30.00") }));

        List<BudgetResponse> responses = budgetService.getAllBudgets();

        assertEquals(new BigDecimal("20.00"), responses.get(0).getSpent());
        assertEquals(new BigDecimal("30.00"), responses.get(1).getSpent());
        verify(expenseRepository).getBudgetTotalsByMonth(
                7, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 8, 1));
        verify(expenseRepository, never()).getTotalSpent(
                anyInt(), anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void insightsRequestBudgetsForSelectedMonthInsteadOfAllBudgets() {
        BudgetService insightBudgetService = mock(BudgetService.class);
        InsightService insightService = new InsightService(
                expenseRepository,
                budgetRepository,
                userRepository,
                insightBudgetService);
        YearMonth month = YearMonth.of(2026, 7);

        insightService.generateInsights(month);

        verify(insightBudgetService).getBudgetsByMonth(month);
        verify(insightBudgetService, never()).getAllBudgets();
    }

    private Budget budget(int id, String category, LocalDate month, String amount) {
        Budget budget = new Budget();
        budget.setId(id);
        budget.setUser(currentUser);
        budget.setCategory(category);
        budget.setMonth(month);
        budget.setAmount(new BigDecimal(amount));
        return budget;
    }
}
