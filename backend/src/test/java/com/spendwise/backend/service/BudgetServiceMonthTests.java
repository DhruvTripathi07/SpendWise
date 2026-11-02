package com.spendwise.backend.service;

import com.spendwise.backend.dto.BudgetRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BudgetServiceMonthTests {

    private static final String USER_EMAIL = "user@example.com";
    private static final LocalDate JULY_FIRST = LocalDate.of(2026, 7, 1);
    private static final LocalDate AUGUST_FIRST = LocalDate.of(2026, 8, 1);

    private BudgetRepository budgetRepository;
    private UserRepository userRepository;
    private ExpenseRepository expenseRepository;
    private BudgetService budgetService;
    private User currentUser;

    @BeforeEach
    void setUp() {
        budgetRepository = mock(BudgetRepository.class);
        userRepository = mock(UserRepository.class);
        expenseRepository = mock(ExpenseRepository.class);
        currentUser = mock(User.class);

        when(currentUser.getId()).thenReturn(7);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(currentUser));
        when(expenseRepository.getTotalSpent(
                anyInt(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
    void createNormalizesJulyFifteenthAndTreatsJulyFirstAsDuplicate() {
        Budget existingBudget = new Budget();
        when(budgetRepository.findByUserIdAndCategoryAndMonth(7, "Food", JULY_FIRST))
                .thenReturn(Optional.empty(), Optional.of(existingBudget));

        BudgetResponse created = budgetService.createBudget(budgetRequest(
                new BigDecimal("100.00"),
                LocalDate.of(2026, 7, 15)));

        assertEquals(YearMonth.of(2026, 7), created.getMonth());
        verify(budgetRepository).save(any(Budget.class));
        verify(expenseRepository).getTotalSpent(
                7, "Food", JULY_FIRST, AUGUST_FIRST);

        assertThrows(IllegalArgumentException.class, () -> budgetService.createBudget(
                budgetRequest(new BigDecimal("100.00"), JULY_FIRST)));
        verify(budgetRepository, times(1)).save(any(Budget.class));
        verify(budgetRepository, times(2))
                .findByUserIdAndCategoryAndMonth(7, "Food", JULY_FIRST);
    }

    @Test
    void updateNormalizesJulyFifteenthAndChecksDuplicatesUsingJulyFirst() {
        Budget budget = ownedBudget(12, LocalDate.of(2026, 6, 1));
        when(budgetRepository.findById(12)).thenReturn(Optional.of(budget));
        when(budgetRepository.findByUserIdAndCategoryAndMonth(7, "Food", JULY_FIRST))
                .thenReturn(Optional.empty());

        BudgetResponse updated = budgetService.updateBudget(
                12,
                budgetRequest(new BigDecimal("150.00"), LocalDate.of(2026, 7, 15)));

        assertEquals(JULY_FIRST, budget.getMonth());
        assertEquals(YearMonth.of(2026, 7), updated.getMonth());
        verify(budgetRepository).findByUserIdAndCategoryAndMonth(7, "Food", JULY_FIRST);
        verify(expenseRepository).getTotalSpent(
                7, "Food", JULY_FIRST, AUGUST_FIRST);
        verify(budgetRepository).save(budget);
    }

    @Test
    void updateDetectsExistingBudgetForSameMonthWhenRequestUsesJulyFirst() {
        Budget budget = ownedBudget(12, LocalDate.of(2026, 6, 1));
        Budget duplicateBudget = ownedBudget(13, JULY_FIRST);
        when(budgetRepository.findById(12)).thenReturn(Optional.of(budget));
        when(budgetRepository.findByUserIdAndCategoryAndMonth(7, "Food", JULY_FIRST))
                .thenReturn(Optional.of(duplicateBudget));

        assertThrows(IllegalArgumentException.class, () -> budgetService.updateBudget(
                12,
                budgetRequest(new BigDecimal("150.00"), JULY_FIRST)));

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void spentAmountUsesFullCalendarMonthEvenForUnnormalizedStoredDate() {
        Budget budget = ownedBudget(12, LocalDate.of(2026, 7, 15));

        budgetService.getSpentAmount(budget);

        verify(expenseRepository).getTotalSpent(
                7, "Food", JULY_FIRST, AUGUST_FIRST);
    }

    private Budget ownedBudget(int id, LocalDate month) {
        Budget budget = new Budget();
        budget.setId(id);
        budget.setUser(currentUser);
        budget.setCategory("Food");
        budget.setMonth(month);
        budget.setAmount(new BigDecimal("100.00"));
        return budget;
    }

    private BudgetRequest budgetRequest(BigDecimal amount, LocalDate month) {
        BudgetRequest request = new BudgetRequest();
        request.setAmount(amount);
        request.setCategory("Food");
        request.setMonth(month);
        return request;
    }
}
