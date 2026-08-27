package com.spendwise.backend.dto;

import java.time.LocalDate;

public class ExpenseResponse {

    private int id;
    private double amount;
    private String category;
    private String description;
    private String paymentMode;
    private LocalDate date;

    public ExpenseResponse() {
    }

    public ExpenseResponse(
            int id,
            double amount,
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

    public double getAmount() {
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