import { formatCurrency } from "../utils/currency";

function QuickInsights({ expenses, summary, budgets }) {

    const totalSpent = Number(summary?.totalSpent || 0);

    const categories = summary?.categories || {};

    const categoryEntries = Object.entries(categories);

    const topCategory = categoryEntries.length > 0
        ? categoryEntries.reduce(
            (highest, current) =>
                current[1] > highest[1]
                    ? current
                    : highest
        )
        : null;

    const averageTransaction =
        expenses.length > 0
            ? totalSpent / expenses.length
            : 0;

    const highestExpense = expenses.length > 0
        ? expenses.reduce(
            (highest, current) =>
                Number(current.amount) > Number(highest.amount)
                    ? current
                    : highest
        )
        : null;

    const overBudgetCount = budgets.filter(
        (budget) => budget.overBudget
    ).length;

    const formatAmount = (amount) => formatCurrency(amount);

    return (
        <div className="quick-insights">

            <div className="insight-item">
                <span className="insight-label">
                    TOP CATEGORY
                </span>

                <strong className="insight-value">
                    {topCategory
                        ? topCategory[0]
                        : "No spending yet"}
                </strong>

                {topCategory && (
                    <span className="insight-caption">
                        {formatAmount(topCategory[1])} spent
                    </span>
                )}
            </div>

            <div className="insight-item">
                <span className="insight-label">
                    AVG. TRANSACTION
                </span>

                <strong className="insight-value">
                    {formatAmount(averageTransaction)}
                </strong>

                <span className="insight-caption">
                    across {expenses.length} transaction
                    {expenses.length === 1 ? "" : "s"}
                </span>
            </div>

            <div className="insight-item">
                <span className="insight-label">
                    LARGEST EXPENSE
                </span>

                <strong className="insight-value">
                    {highestExpense
                        ? formatAmount(highestExpense.amount)
                        : formatCurrency(0)}
                </strong>

                <span className="insight-caption">
                    {highestExpense?.category || "No expenses yet"}
                </span>
            </div>

            <div className="insight-item">
                <span className="insight-label">
                    BUDGET STATUS
                </span>

                <strong className="insight-value">
                    {overBudgetCount === 0
                        ? "On track"
                        : `${overBudgetCount} over budget`}
                </strong>

                <span className="insight-caption">
                    {budgets.length === 0
                        ? "No budgets set"
                        : `${budgets.length} budget${
                            budgets.length === 1 ? "" : "s"
                        } this month`}
                </span>
            </div>

        </div>
    );
}

export default QuickInsights;