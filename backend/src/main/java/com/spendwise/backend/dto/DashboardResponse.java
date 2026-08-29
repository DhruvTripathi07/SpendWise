package com.spendwise.backend.dto;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class DashboardResponse {

    private YearMonth month;
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> categories;
    private List<BudgetResponse> budgets;
    private List<ExpenseResponse> recentExpenses;

    public DashboardResponse() {
    }

    public DashboardResponse(
            YearMonth month,
            BigDecimal totalSpent,
            Map<String, BigDecimal> categories,
            List<BudgetResponse> budgets,
            List<ExpenseResponse> recentExpenses) {

        this.month = month;
        this.totalSpent = totalSpent;
        this.categories = categories;
        this.budgets = budgets;
        this.recentExpenses = recentExpenses;
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

    public List<BudgetResponse> getBudgets() {
        return budgets;
    }

    public List<ExpenseResponse> getRecentExpenses() {
        return recentExpenses;
    }
}
