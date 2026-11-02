package com.spendwise.backend.controller;

import com.spendwise.backend.service.ExpenseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseControllerPaginationTests {

    private ExpenseService expenseService;
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        expenseService = mock(ExpenseService.class);
        expenseController = new ExpenseController(expenseService);
    }

    @Test
    void defaultsToPageZeroAndSizeTwenty() {
        Pageable pageable = pageable(0, 20);
        when(expenseService.getExpenses(null, null, 0, 20))
                .thenReturn(Page.empty(pageable));

        expenseController.getAllExpenses(null, null, 0, 20);

        verify(expenseService).getExpenses(null, null, 0, 20);
    }

    @Test
    void capsRequestedSizeAtOneHundred() {
        Pageable pageable = pageable(3, 100);
        when(expenseService.getExpenses("Food", null, 3, 100))
                .thenReturn(Page.empty(pageable));

        expenseController.getAllExpenses("Food", null, 3, 500);

        verify(expenseService).getExpenses("Food", null, 3, 100);
    }

    @Test
    void rejectsNegativePageAndNonPositiveSize() {
        ResponseStatusException negativePage = assertThrows(
                ResponseStatusException.class,
                () -> expenseController.getAllExpenses(null, null, -1, 20));
        ResponseStatusException zeroSize = assertThrows(
                ResponseStatusException.class,
                () -> expenseController.getAllExpenses(null, null, 0, 0));

        assertEquals(400, negativePage.getStatusCode().value());
        assertEquals(400, zeroSize.getStatusCode().value());
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id")));
    }
}
