package com.spendwise.backend.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.math.BigDecimal;

public class BudgetResponse {

    private int id;
    private BigDecimal amount;
    private String category;
    private YearMonth month;
    private BigDecimal spent;
    private BigDecimal remaining;
    private BigDecimal progress;
    private BigDecimal progressPercent;
    private boolean overBudget;

    public BudgetResponse() {
    }

    public BudgetResponse(
            int id,
            BigDecimal amount,
            String category,
            YearMonth month,
            BigDecimal spent,
            BigDecimal remaining,
            BigDecimal progress,
            BigDecimal progressPercent,
            boolean overBudget) {

        this.id = id;
        this.amount = amount;
        this.category = category;
        this.month = month;
        this.spent = spent;
        this.remaining = remaining;
        this.progress = progress;
        this.progressPercent = progressPercent;
        this.overBudget = overBudget;
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

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public BigDecimal getProgressPercent() {
        return progressPercent;
    }

    public boolean isOverBudget() {
        return overBudget;
    }
}
