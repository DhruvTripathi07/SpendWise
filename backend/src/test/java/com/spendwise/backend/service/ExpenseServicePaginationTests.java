package com.spendwise.backend.service;

import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServicePaginationTests {

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
    void noFiltersUsePaginatedUserScopedHistoricalQuery() {
        Pageable pageable = pageable(2, 20);
        when(expenseRepository.findAllByUserId(eq(7), any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        Page<?> result = expenseService.getExpenses(null, null, 2, 20);

        assertEquals(2, result.getNumber());
        verify(expenseRepository).findAllByUserId(eq(7), eq(pageable));
        assertSorting(pageable);
    }

    @Test
    void categoryFilterUsesUserScopedPaginatedQuery() {
        Pageable pageable = pageable(0, 20);
        when(expenseRepository.findAllByUserIdAndCategory(
                eq(7), eq("Food"), any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        expenseService.getExpenses("Food", null, 0, 20);

        verify(expenseRepository).findAllByUserIdAndCategory(
                eq(7), eq("Food"), eq(pageable));
        assertSorting(pageable);
    }

    @Test
    void monthFilterUsesUserScopedDateRangeQuery() {
        YearMonth month = YearMonth.of(2026, 7);
        Pageable pageable = pageable(0, 20);
        when(expenseRepository.findAllByUserIdAndDateGreaterThanEqualAndDateLessThan(
                eq(7),
                eq(month.atDay(1)),
                eq(month.plusMonths(1).atDay(1)),
                any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        expenseService.getExpenses(null, month, 0, 20);

        verify(expenseRepository).findAllByUserIdAndDateGreaterThanEqualAndDateLessThan(
                eq(7),
                eq(month.atDay(1)),
                eq(month.plusMonths(1).atDay(1)),
                eq(pageable));
        assertSorting(pageable);
    }

    @Test
    void monthAndCategoryUseUserScopedPaginatedDateRangeQuery() {
        YearMonth month = YearMonth.of(2026, 7);
        Pageable pageable = pageable(1, 15);
        when(expenseRepository
                .findAllByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
                        eq(7),
                        eq("Food"),
                        eq(month.atDay(1)),
                        eq(month.plusMonths(1).atDay(1)),
                        any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        expenseService.getExpenses("Food", month, 1, 15);

        verify(expenseRepository)
                .findAllByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
                        eq(7),
                        eq("Food"),
                        eq(month.atDay(1)),
                        eq(month.plusMonths(1).atDay(1)),
                        eq(pageable));
        assertSorting(pageable);
    }

    @Test
    void blankCategoryMeansNoCategoryFilterAndSizeIsCappedAtOneHundred() {
        Pageable pageable = pageable(0, 100);
        when(expenseRepository.findAllByUserId(eq(7), any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        expenseService.getExpenses("  ", null, 0, 250);

        verify(expenseRepository).findAllByUserId(eq(7), eq(pageable));
        assertSorting(pageable);
    }

    @Test
    void expenseCategoriesAreScopedToUserAndSelectedMonth() {
        YearMonth month = YearMonth.of(2026, 7);
        when(expenseRepository.findCategoriesByUserIdAndDateRange(
                7, month.atDay(1), month.plusMonths(1).atDay(1)))
                .thenReturn(List.of("Food", "Travel"));

        List<String> categories = expenseService.getExpenseCategories(month);

        assertEquals(List.of("Food", "Travel"), categories);
        verify(expenseRepository).findCategoriesByUserIdAndDateRange(
                7, month.atDay(1), month.plusMonths(1).atDay(1));
    }

    @Test
    void expenseCategoriesWithoutMonthAreScopedToUser() {
        when(expenseRepository.findCategoriesByUserId(7)).thenReturn(List.of("Food"));

        List<String> categories = expenseService.getExpenseCategories(null);

        assertEquals(List.of("Food"), categories);
        verify(expenseRepository).findCategoriesByUserId(7);
    }

    private Pageable pageable(int page, int size) {
        return org.springframework.data.domain.PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Order.desc("date"),
                        org.springframework.data.domain.Sort.Order.desc("id")));
    }

    private void assertSorting(Pageable pageable) {
        assertEquals(List.of("date", "id"),
                pageable.getSort().stream().map(order -> order.getProperty()).toList());
        assertEquals(List.of(
                        org.springframework.data.domain.Sort.Direction.DESC,
                        org.springframework.data.domain.Sort.Direction.DESC),
                pageable.getSort().stream().map(order -> order.getDirection()).toList());
    }
}
