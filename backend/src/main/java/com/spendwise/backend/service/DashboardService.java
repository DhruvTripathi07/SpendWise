package com.spendwise.backend.service;

import com.spendwise.backend.dto.DashboardResponse;
import com.spendwise.backend.dto.ExpenseResponse;
import com.spendwise.backend.dto.MonthlySummaryResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    public DashboardService(
            ExpenseService expenseService,
            BudgetService budgetService) {

        this.expenseService = expenseService;
        this.budgetService = budgetService;
    }

    public DashboardResponse getDashboard(YearMonth month) {

        MonthlySummaryResponse summary = expenseService.getMonthlySummary(month);

        List<ExpenseResponse> recentExpenses = expenseService.getExpensesByMonth(month.atDay(1));

        return new DashboardResponse(
                summary.getMonth(),
                summary.getTotalSpent(),
                summary.getCategories(),
                budgetService.getAllBudgets(),
                recentExpenses);
    }
}
