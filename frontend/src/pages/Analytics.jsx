import { useEffect, useState } from "react";
import api from "../services/api";
import AppLayout from "../components/AppLayout";
import CategorySpendingChart from "../components/CategorySpendingChart";
import SixMonthSpendingChart from "../components/SixMonthSpendingChart";
import BudgetVsActualChart from "../components/BudgetVsActualChart";
import QuickInsights from "../components/QuickInsights";
import { formatCurrency } from "../utils/currency";
import "../App.css";
import DailySpendingChart from "../components/DailySpendingChart";

function Analytics() {
    const getCurrentMonth = () => {
        const now = new Date();
        return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
    };
    const [month, setMonth] = useState(getCurrentMonth());
    const [expenses, setExpenses] = useState([]);
    const [summary, setSummary] = useState(null);
    const [monthlySpending, setMonthlySpending] = useState([]);
    const [budgets, setBudgets] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const changeMonth = (amount) => {
        const [year, currentMonth] = month.split("-");

        const date = new Date(
            Number(year),
            Number(currentMonth) - 1 + amount
        );

        const newYear = date.getFullYear();
        const newMonth = String(date.getMonth() + 1).padStart(2, "0");

        setMonth(`${newYear}-${newMonth}`);
    };

    useEffect(() => {
        const fetchAnalytics = async () => {
            setLoading(true);
            setError("");

            try {
                const [expensesResponse, summaryResponse, monthlyResponse, budgetsResponse] =
                    await Promise.all([
                        api.get(`/expenses?month=${month}`),
                        api.get(`/expenses/summary?month=${month}`),
                        api.get(`/expenses/monthly-spending?month=${month}`),
                        api.get(`/budgets?month=${month}`),
                    ]);

                setExpenses(expensesResponse.data);
                setSummary(summaryResponse.data);
                setMonthlySpending(monthlyResponse.data);
                setBudgets(budgetsResponse.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load analytics.");
            } finally {
                setLoading(false);
            }
        };

        fetchAnalytics();
    }, [month]);

    const displayMonth = new Date(`${month}-01`).toLocaleDateString(
        "en-IN",
        {
            month: "long",
            year: "numeric",
        }
    );

    if (loading) {
        return <AppLayout>Loading analytics...</AppLayout>;
    }

    if (error) {
        return <AppLayout>{error}</AppLayout>;
    }

    return (
        <AppLayout>
                    <header className="dashboard-header">
                        <div>
                            <h1 className="dashboard-title">
                                Analytics
                            </h1>

                            <p className="dashboard-subtitle">
                                Keeping an eye on the bigger picture
                            </p>
                        </div>

                        <div className="month-controls">
                            <button
                                className="month-button"
                                onClick={() => changeMonth(-1)}
                            >
                                ‹
                            </button>

                            <div className="month-display">
                                ◫ {displayMonth}
                            </div>

                            <button
                                className="month-button"
                                onClick={() => changeMonth(1)}
                            >
                                ›
                            </button>
                        </div>
                    </header>

                    <section className="summary-grid">
                        <div className="summary-card">
                            <span className="summary-label">
                                TOTAL SPENT
                            </span>

                            <div className="summary-value">
                                {formatCurrency(summary?.totalSpent || 0)}
                            </div>

                            <span className="summary-caption">
                                this month
                            </span>
                        </div>

                        <div className="summary-card">
                            <span className="summary-label">
                                EXPENSES
                            </span>

                            <div className="summary-value">
                                {expenses.length}
                            </div>

                            <span className="summary-caption">
                                transactions this month
                            </span>
                        </div>

                        <div className="summary-card">
                            <span className="summary-label">
                                Average Transaction
                            </span>

                            <div className="summary-value">
                                {formatCurrency(
                                    Number(summary?.totalSpent || 0) /
                                    Math.max(expenses.length, 1)
                                )}
                            </div>

                            <span className="summary-caption">
                                per transaction
                            </span>
                        </div>
                    </section>

                    <section className="content-grid">
                        <div className="recent-card">
                            <div className="section-heading">
                                <div>
                                    <h2>Daily Spending</h2>
                                    <p>
                                        Your spending throughout{" "}
                                        {displayMonth}
                                    </p>
                                </div>
                            </div>

                            <div className="analytics-placeholder">
                                <DailySpendingChart expenses={expenses} month={month} />
                            </div>
                        </div>

                        <div className="recent-card">
                            <div className="section-heading">
                                <div>
                                    <h2>Spending by Category</h2>
                                    <p>
                                        Where your money went this month
                                    </p>
                                </div>
                            </div>

                            <CategorySpendingChart
                                categories={summary?.categories}
                            />
                        </div>
                    </section>

                    <section className="recent-card">
                        <div className="section-heading">
                            <div>
                                <h2>Six-Month Spending</h2>
                                <p>
                                    A look at your spending over the last six
                                    months
                                </p>
                            </div>
                        </div>

                        <div className="analytics-placeholder">
                            <SixMonthSpendingChart
                                monthlySpending={monthlySpending}
                            />
                        </div>
                    </section>
                    <section className="recent-card">
                        <div className="section-heading">
                            <div>
                                <h2>Budget vs Actual</h2>
                                <p>
                                    See how your spending compares with your budgets
                                </p>
                            </div>
                        </div>

                        <BudgetVsActualChart
                            budgets={budgets}
                        />
                    </section>
                    <section className="recent-card">
                        <div className="section-heading">
                            <div>
                                <h2>Quick Insights</h2>
                                <p>
                                    A few things worth knowing about your spending
                                </p>
                            </div>
                        </div>

                        <QuickInsights
                            expenses={expenses}
                            summary={summary}
                            budgets={budgets}
                        />
                    </section>
        </AppLayout>
    );
}

export default Analytics;