package com.spendwise.backend.service;

import com.spendwise.backend.dto.DashboardResponse;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;
import com.spendwise.backend.model.Expense;
import com.spendwise.backend.model.User;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardRecentExpensesTests {

    private static final String USER_EMAIL = "user@example.com";
    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private ExpenseService expenseService;
    private User currentUser;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        userRepository = mock(UserRepository.class);
        currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(7);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(currentUser));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_EMAIL, "password"));
        expenseService = new ExpenseService(expenseRepository, userRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recentExpensesAreQueriedForUserAndSelectedMonth() {
        Expense newest = expense(2, LocalDate.of(2026, 7, 31));
        Expense older = expense(1, LocalDate.of(2026, 7, 2));
        when(expenseRepository
                .findTop5ByUserIdAndDateGreaterThanEqualAndDateLessThanOrderByDateDescIdDesc(
                        7,
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 1)))
                .thenReturn(List.of(newest, older));

        List<ExpenseResponse> recent = expenseService.getRecentExpensesByMonth(
                LocalDate.of(2026, 7, 15));

        assertEquals(List.of(2, 1), recent.stream().map(ExpenseResponse::getId).toList());
        verify(expenseRepository)
                .findTop5ByUserIdAndDateGreaterThanEqualAndDateLessThanOrderByDateDescIdDesc(
                        7,
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 1));
    }

    @Test
    void dashboardUsesRecentExpensesWithoutChangingItsResponseShape() {
        ExpenseService expenseService = mock(ExpenseService.class);
        BudgetService budgetService = mock(BudgetService.class);
        YearMonth month = YearMonth.of(2026, 7);
        List<ExpenseResponse> recent = List.of(
                new ExpenseResponse(2, BigDecimal.TEN, "Food", "Dinner", "Cash",
                        LocalDate.of(2026, 7, 31)));
        MonthlySummaryResponse summary = new MonthlySummaryResponse(
                month,
                BigDecimal.TEN,
                Map.of("Food", BigDecimal.TEN));
        when(expenseService.getMonthlySummary(month)).thenReturn(summary);
        when(expenseService.getRecentExpensesByMonth(month.atDay(1))).thenReturn(recent);

        DashboardResponse response =
                new DashboardService(expenseService, budgetService).getDashboard(month);

        assertEquals(recent, response.getRecentExpenses());
        verify(expenseService).getRecentExpensesByMonth(month.atDay(1));
    }

    private Expense expense(int id, LocalDate date) {
        Expense expense = new Expense();
        expense.setId(id);
        expense.setAmount(BigDecimal.TEN);
        expense.setCategory("Food");
        expense.setDate(date);
        expense.setUser(currentUser);
        return expense;
    }
}
