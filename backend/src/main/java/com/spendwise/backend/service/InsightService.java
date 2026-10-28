package com.spendwise.backend.service;

import com.spendwise.backend.dto.InsightResponse;
import com.spendwise.backend.dto.BudgetResponse;
import com.spendwise.backend.model.User;
import com.spendwise.backend.repository.UserRepository;
import com.spendwise.backend.repository.ExpenseRepository;
import com.spendwise.backend.repository.BudgetRepository;
import com.spendwise.backend.exception.ResourceNotFoundException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsightService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    public InsightService(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            BudgetService budgetService) {

        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
    }

    public List<InsightResponse> generateInsights(YearMonth month) {

        User currentUser = getCurrentUser();

        List<InsightResponse> insights = new ArrayList<>();

        addMonthlySpendingInsight(currentUser, month, insights);
        addCategorySpendingInsight(currentUser, month, insights);
        addBudgetInsights(currentUser, month, insights);

        insights.sort(
                        Comparator.comparingInt(
                                        insight -> getInsightPriority(insight.getType())));

        return insights;
    }

    private void addMonthlySpendingInsight(
            User user,
            YearMonth month,
            List<InsightResponse> insights) {

        LocalDate currentStart = month.atDay(1);
        LocalDate currentEnd = month.plusMonths(1).atDay(1);

        YearMonth previousMonth = month.minusMonths(1);

        LocalDate previousStart = previousMonth.atDay(1);
        LocalDate previousEnd = month.atDay(1);

        BigDecimal currentTotal = expenseRepository.getMonthlyTotals(
                user.getId(), currentStart, currentEnd)
                .stream()
                .map(row -> (BigDecimal) row.get("total"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousTotal = expenseRepository.getMonthlyTotals(
                user.getId(), previousStart, previousEnd)
                .stream()
                .map(row -> (BigDecimal) row.get("total"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (previousTotal.compareTo(BigDecimal.ZERO) == 0) {
                if (currentTotal.compareTo(BigDecimal.ZERO) > 0) {
                        insights.add(new InsightResponse(
                                        "SPENDING_STARTED",
                                        "Spending activity this month",
                                        "You've recorded "
                                                        + currentTotal.setScale(2, RoundingMode.HALF_UP)
                                                                        .toPlainString()
                                                        + " in spending this month, compared with no recorded "
                                                        + "spending last month.",
                                        "Keep tracking your expenses to establish a spending baseline."));
                }
                return;
        }

        BigDecimal difference = currentTotal.subtract(previousTotal);

        BigDecimal percentage = difference
                .multiply(BigDecimal.valueOf(100))
                .divide(previousTotal, 2, RoundingMode.HALF_UP);

        if (percentage.compareTo(BigDecimal.valueOf(10)) >= 0) {

            insights.add(new InsightResponse(
                    "SPENDING_INCREASE",
                    "Spending has increased",
                    "Your spending increased by "
                            + percentage.stripTrailingZeros().toPlainString()
                            + "% compared with "
                            + previousMonth + ".",
                    "Compare your spending by category with last month to identify where "
                            + "the increase occurred, then review any non-essential expenses."));

        } else if (percentage.compareTo(BigDecimal.valueOf(-10)) <= 0) {

            insights.add(new InsightResponse(
                    "SPENDING_DECREASE",
                    "Spending is down",
                    "Your spending decreased by "
                            + percentage.abs().stripTrailingZeros().toPlainString()
                            + "% compared with "
                            + previousMonth + ".",
                    "Keep tracking your expenses to understand which changes helped."));
        }
    }

        private void addCategorySpendingInsight(
                User user,
                YearMonth month,
                List<InsightResponse> insights) {

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        List<java.util.Map<String, Object>> categoryTotals =
                expenseRepository.getCategoryTotals(
                        user.getId(),
                        startDate,
                        endDate);

        if (categoryTotals.isEmpty()) {
                return;
        }

        java.util.Map<String, Object> highestCategory = categoryTotals
                .stream()
                .max((first, second) ->
                        ((BigDecimal) first.get("total"))
                                .compareTo((BigDecimal) second.get("total")))
                .orElse(null);

        if (highestCategory == null) {
                return;
        }

        String category = (String) highestCategory.get("category");
        BigDecimal amount = (BigDecimal) highestCategory.get("total");

        insights.add(new InsightResponse(
                "TOP_SPENDING_CATEGORY",
                "Your biggest spending category",
                category + " is your highest spending category this month, "
                        + "at " + amount.setScale(2, RoundingMode.HALF_UP)
                                .toPlainString() + ".",
                "Review your " + category
                        + " expenses for recurring costs or non-essesntial purchases "
                        + "that could be reduced next month."
        ));
        }


        private void addBudgetInsights(
                User user,
                YearMonth month,
                List<InsightResponse> insights) {

                List<BudgetResponse> budgets = budgetService.getAllBudgets()
                        .stream()
                        .filter(budget -> budget.getMonth().equals(month))
                        .toList();

                for (BudgetResponse budget : budgets) {

                if (budget.isOverBudget()) {

                        BigDecimal overspent = budget.getSpent()
                                .subtract(budget.getAmount());

                        insights.add(new InsightResponse(
                                "BUDGET_EXCEEDED",
                                budget.getCategory() + " budget exceeded",
                                "You've exceeded your "
                                        + budget.getCategory()
                                        + " budget by "
                                        + overspent.toPlainString() + ".",
                                "Review recent " + budget.getCategory()
                                        + " expenses and consider adjusting upcoming purchases."));

                } else if (budget.getProgressPercent()
                        .compareTo(BigDecimal.valueOf(80)) >= 0) {

                        insights.add(new InsightResponse(
                                "BUDGET_WARNING",
                                budget.getCategory() + " budget running low",
                                "You've used "
                                        + budget.getProgressPercent()
                                                .setScale(0, RoundingMode.HALF_UP)
                                                .toPlainString()
                                        + "% of your "
                                        + budget.getCategory()
                                        + " budget.",
                                "Check your remaining budget before spending more"
                                        + " in this category to avoid exceeding it."));
                }
                }
        }

        private int getInsightPriority(String type) {
                return switch (type) {
                        case "BUDGET_EXCEEDED" -> 1;
                        case "BUDGET_WARNING" -> 2;
                        case "SPENDING_INCREASE" -> 3;
                        case "TOP_SPENDING_CATEGORY" -> 4;
                        case "SPENDING_DECREASE" -> 5;
                        case "SPENDING_STARTED" -> 6;
                        default -> 7;
                };
        }
        private User getCurrentUser() {

                String email = SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

                return userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Authenticated user not found"));
        }
}