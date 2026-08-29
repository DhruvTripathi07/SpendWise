package com.spendwise.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ExpenseRequest {

    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "category must not be blank")
    private String category;

    private String description;

    @NotBlank(message = "paymentMode must not be blank")
    private String paymentMode;

    @NotNull(message = "date must not be null")
    private LocalDate date;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}