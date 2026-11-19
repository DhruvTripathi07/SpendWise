package com.spendwise.backend.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void duplicateBudgetConstraintShouldReturn409() {

        ConstraintViolationException constraintException = new ConstraintViolationException(
                "Duplicate budget",
                null,
                "uk_budget_user_category_month");

        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Database constraint violation",
                constraintException);

        ResponseEntity<?> response = handler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void unrelatedConstraintViolationShouldBeRethrown() {

        ConstraintViolationException constraintException = new ConstraintViolationException(
                "Some other constraint",
                null,
                "some_other_constraint");

        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Database constraint violation",
                constraintException);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> handler.handleDataIntegrityViolation(exception));
    }
}