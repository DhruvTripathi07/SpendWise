package com.spendwise.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonetaryRequestValidationTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void rejectsNullAmountForExpenseAndBudgetRequests() {
        assertAmountIsInvalid(expenseRequest(null));
        assertAmountIsInvalid(budgetRequest(null));
    }

    @Test
    void rejectsZeroAmountForExpenseAndBudgetRequests() {
        assertAmountIsInvalid(expenseRequest(BigDecimal.ZERO));
        assertAmountIsInvalid(budgetRequest(BigDecimal.ZERO));
    }

    @Test
    void rejectsNegativeAmountForExpenseAndBudgetRequests() {
        BigDecimal negativeAmount = new BigDecimal("-0.01");

        assertAmountIsInvalid(expenseRequest(negativeAmount));
        assertAmountIsInvalid(budgetRequest(negativeAmount));
    }

    @Test
    void acceptsPositiveAmountForExpenseAndBudgetRequests() {
        BigDecimal positiveAmount = new BigDecimal("12.34");

        assertTrue(validator.validate(expenseRequest(positiveAmount)).isEmpty());
        assertTrue(validator.validate(budgetRequest(positiveAmount)).isEmpty());
    }

    private void assertAmountIsInvalid(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("amount", violations.iterator().next().getPropertyPath().toString());
    }

    private ExpenseRequest expenseRequest(BigDecimal amount) {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(amount);
        request.setCategory("Food");
        request.setPaymentMode("Cash");
        request.setDate(LocalDate.of(2026, 1, 1));
        return request;
    }

    private BudgetRequest budgetRequest(BigDecimal amount) {
        BudgetRequest request = new BudgetRequest();
        request.setAmount(amount);
        request.setCategory("Food");
        request.setMonth(LocalDate.of(2026, 1, 1));
        return request;
    }
}
