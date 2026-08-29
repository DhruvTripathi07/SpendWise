package com.spendwise.backend.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.math.BigDecimal;;

public class MonthlySummaryResponse {

    private YearMonth month;
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> categories;

    public MonthlySummaryResponse() {
    }

    public MonthlySummaryResponse(
            YearMonth month,
            BigDecimal totalSpent,
            Map<String, BigDecimal> categories) {

        this.month = month;
        this.totalSpent = totalSpent;
        this.categories = categories;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public Map<String, BigDecimal> getCategories() {
        return categories;
    }
}