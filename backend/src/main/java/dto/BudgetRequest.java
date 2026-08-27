package com.spendwise.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class BudgetRequest {

    @Positive(message = "amount must be greater than 0")
    private double amount;

    @NotBlank(message = "category must not be blank")
    private String category;

    @NotNull(message = "month must not be null")
    private LocalDate month;

    public BudgetRequest() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }
}