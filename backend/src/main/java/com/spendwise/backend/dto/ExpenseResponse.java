package com.spendwise.backend.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ExpenseResponse {

    private int id;
    private BigDecimal amount;
    private String category;
    private String description;
    private String paymentMode;
    private LocalDate date;

    public ExpenseResponse() {
    }

    public ExpenseResponse(
            int id,
            BigDecimal amount,
            String category,
            String description,
            String paymentMode,
            LocalDate date) {

        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.paymentMode = paymentMode;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public LocalDate getDate() {
        return date;
    }
}