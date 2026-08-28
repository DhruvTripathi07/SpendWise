package com.spendwise.backend.dto;

import java.time.LocalDate;
import java.util.Map;

public class MonthlySummaryResponse {

    private LocalDate month;
    private double totalSpent;
    private Map<String, Double> categories;

    public MonthlySummaryResponse() {
    }

    public MonthlySummaryResponse(
            LocalDate month,
            double totalSpent,
            Map<String, Double> categories) {

        this.month = month;
        this.totalSpent = totalSpent;
        this.categories = categories;
    }

    public LocalDate getMonth() {
        return month;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public Map<String, Double> getCategories() {
        return categories;
    }
}