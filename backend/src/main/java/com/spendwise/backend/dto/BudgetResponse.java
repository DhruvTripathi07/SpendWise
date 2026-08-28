package com.spendwise.backend.dto;

import java.time.LocalDate;

public class BudgetResponse {

    private int id;
    private double amount;
    private String category;
    private LocalDate month;
    private double spent;
    private double remaining;
    private double progress;

    public BudgetResponse() {
    }

    public BudgetResponse(
            int id,
            double amount,
            String category,
            LocalDate month,
            double spent,
            double remaining,
            double progress) {

        this.id = id;
        this.amount = amount;
        this.category = category;
        this.month = month;
        this.spent = spent;
        this.remaining = remaining;
        this.progress = progress;
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

    public LocalDate getMonth() {
        return month;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return remaining;
    }

    public double getProgress() {
        return progress;
    }
}
