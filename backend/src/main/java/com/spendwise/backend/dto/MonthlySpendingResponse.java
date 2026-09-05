package com.spendwise.backend.dto;


import java.math.BigDecimal;
import java.time.YearMonth;

public class MonthlySpendingResponse {
    private YearMonth month;
    private BigDecimal totalSpent;

    public MonthlySpendingResponse(
            YearMonth month,
            BigDecimal totalSpent) {

        this.month = month;
        this.totalSpent = totalSpent;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
}
