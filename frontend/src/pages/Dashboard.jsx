import { useEffect, useState } from "react";
import api from "../services/api";
import { formatCurrency } from "../utils/currency";
import AppLayout from "../components/AppLayout";

function Dashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [insights, setInsights] = useState([]);
    const [insightsExpanded, setInsightsExpanded] = useState(false);
    const [error, setError] = useState("");

    const getCurrentMonth = () => {
            const now = new Date();

            return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
        };

    const [month, setMonth] = useState(getCurrentMonth());

    const changeMonth = (amount) => {
        const [year, currentMonth] = month.split("-");

        const date = new Date(Number(year), Number(currentMonth) - 1 + amount);

        const newYear = date.getFullYear();
        const newMonth = String(date.getMonth() + 1).padStart(2, "0");

        setMonth(`${newYear}-${newMonth}`);
    };

    useEffect(() => {
        const fetchDashboard = async () => {
            try {
                const response = await api.get(`/dashboard?month=${month}`);
                setDashboard(response.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load dashboard.");
            } finally {
                setLoading(false);
            }
        };
        fetchDashboard();
    }, [month]);
    
    useEffect(() => {
        const fetchInsights = async () => {
            try {
                const response = await api.get(`/insights?month=${month}`);
                setInsights(response.data);
            } catch (err) {
                console.error("Failed to load insights:", err);
            }
        };
        fetchInsights();
    }, [month]);
    


    if (loading) {
        return <div className="app loading-state">Loading dashboard...</div>;
    }

    if (error) {
        return <div className="app error-state">{error}</div>;
    }

    const totalSpent = Number(dashboard.totalSpent || 0);

    const totalBudget = dashboard.budgets.reduce(
        (sum, budget) => sum + Number(budget.amount || 0),
        0
    );

    const remaining = totalBudget - totalSpent;

    const displayMonth = new Date(`${month}-01`).toLocaleDateString(
        "en-IN",
        {
            month: "long",
            year: "numeric",
        }
    );

    return (
        <AppLayout>

                    {/* Dashboard Header */}

                    <header className="dashboard-header">

                        <div>
                            
                            <h1 className="dashboard-title">
                                Financial Overview
                            </h1>

                            <p className="dashboard-subtitle">
                                Keeping an eye on your finances, as one should!
                            </p>
                        </div>

                        <div className="month-controls">

                            <button className="month-button" onClick={() => changeMonth(-1)}>
                                ‹
                            </button>

                            <div className="month-display">
                                ◫ {displayMonth}
                            </div>

                            <button className="month-button" onClick={() => changeMonth(1)}>
                                ›
                            </button>

                        </div>

                    </header>

                    {/* Summary Cards */}

                    <section className="summary-grid">

                        <div className="card summary-card">
                            <div className="card-label">
                                TOTAL SPENT
                            </div>

                            <div className="money">
                                {formatCurrency(totalSpent)}
                            </div>

                            <div className="card-caption">
                                this month
                            </div>
                        </div>

                        <div className="card summary-card">
                            <div className="card-label">
                                MONTHLY BUDGET
                            </div>

                            <div className="money">
                                {formatCurrency(totalBudget)}
                            </div>

                            <div className="card-caption">
                                for {displayMonth}
                            </div>
                        </div>

                        <div className="card summary-card">
                            <div className="card-label">
                                AMOUNT LEFT
                            </div>

                            <div className="money accent">
                                {formatCurrency(remaining)}
                            </div>

                            <div className="card-caption">
                                remaining budget
                            </div>
                        </div>

                    </section>

                    {/* Category + Budget + Insights */}

                    <section className="content-grid">

                        {/* Category Breakdown */}

                        <div className="card content-card">

                            <div className="card-heading">

                                <h2 className="card-title">
                                    SPENDING BY CATEGORY
                                </h2>

                                <span className="card-link">
                                    By Amount
                                </span>

                            </div>

                            <div className="category-content">

                                <div className="donut">

                                    <div className="donut-inner">

                                        <div>

                                            <div className="donut-label">
                                                Total
                                            </div>

                                            <div className="donut-total">
                                                {formatCurrency(totalSpent)}
                                            </div>

                                        </div>

                                    </div>

                                </div>

                                <div className="category-list">

                                    {Object.entries(dashboard.categories).map(
                                        ([category, amount], index) => {

                                            const percentage =
                                                totalSpent > 0
                                                    ? (Number(amount) / totalSpent) * 100
                                                    : 0;

                                            const dots = [
                                                "#9aa19e",
                                                "#7560c8",
                                                "#4c9ac5",
                                                "#b38e43",
                                                "#626870"
                                            ];

                                            return (
                                                <div
                                                    className="category-row"
                                                    key={category}
                                                >

                                                    <span
                                                        className="category-dot"
                                                        style={{
                                                            background:
                                                                dots[index % dots.length]
                                                        }}
                                                    />

                                                    <span className="category-name">
                                                        {category}
                                                    </span>

                                                    <span className="category-amount">
                                                        {formatCurrency(amount)}
                                                    </span>

                                                    <span className="category-percent">
                                                        {percentage.toFixed(1)}%
                                                    </span>

                                                </div>
                                            );
                                        }
                                    )}

                                </div>

                            </div>

                        </div>

                        {/* Budget Overview */}

                        <div className="card content-card">

                            <div className="card-heading">

                                <h2 className="card-title">
                                    BUDGET OVERVIEW
                                </h2>

                                <span className="card-link">
                                    View all budgets →
                                </span>

                            </div>

                            <div className="budget-list">

                                {dashboard.budgets.map((budget) => {

                                    const progressBar = Math.min(
                                        Number(
                                            budget.progressPercent ?? 0),
                                        100
                                    );

                                    const overBudget =
                                        budget.overBudget === true;

                                    return (
                                        <div
                                            className="budget-row"
                                            key={budget.id}
                                        >

                                            <div className="budget-top">

                                                <span className="budget-category">
                                                    {budget.category}
                                                </span>

                                                <span className="budget-value">
                                                    {formatCurrency(budget.spent)}
                                                    {" / "}
                                                    {formatCurrency(budget.amount)}
                                                </span>

                                            </div>

                                            <div className="progress-track">

                                                <div
                                                    className={`progress-bar ${
                                                        overBudget
                                                            ? "over-budget"
                                                            : ""
                                                    }`}
                                                    style={{
                                                        width: `${progressBar}%`
                                                    }}
                                                />

                                            </div>

                                            <div className="budget-bottom">
                                                <span className="budget-remaining">
                                                    {formatCurrency(budget.remaining)} remaining
                                                </span>

                                                <span className="budget-percent">
                                                    {Number(budget.progress ?? 0).toFixed(2)}%
                                                </span>
                                            </div>

                                        </div>
                                    );
                                })}

                            </div>

                        </div>

                        {/* Monthly Insights */}
                        <section className={`card monthly-insights-card ${
                            insightsExpanded ? "expanded" : ""
                        }`}>
                            <div className="monthly-insights-header">
                                <div>
                                    <h2 className="card-title">MONTHLY INSIGHTS</h2>
                                    <p className="monthly-insights-subtitle">
                                        Personalised observations for {displayMonth}
                                    </p>
                                </div>

                                {insights.length > 0 && (
                                    <button
                                        className="insights-toggle"
                                        onClick={() => setInsightsExpanded(!insightsExpanded)}
                                        aria-expanded={insightsExpanded}
                                    >
                                        {insightsExpanded ? "Show less ↑" : "Explore insights →"}
                                    </button>
                                )}
                            </div>

                            {insights.length === 0 ? (
                                <p className="monthly-insights-empty">
                                    No notable spending patterns yet. Keep tracking your expenses
                                    to unlock personalised insights.
                                </p>
                            ) : (
                                <div className="monthly-insights-list">
                                    {(insightsExpanded ? insights : insights.slice(0, 3)).map(
                                        (insight, index) => (
                                            <article
                                                className="monthly-insight-item"
                                                key={`${insight.type}-${index}`}
                                            >
                                                <div className="monthly-insight-content">
                                                    <h3>{insight.title}</h3>
                                                    <p>{insight.message}</p>

                                                    {insightsExpanded && (
                                                        <div className="monthly-insight-recommendation">
                                                            <span>RECOMMENDATION</span>
                                                            <p>{insight.recommendation}</p>
                                                        </div>
                                                    )}
                                                </div>
                                            </article>
                                        )
                                    )}
                                </div>
                            )}
                        </section> {/* End of Monthly Insights */}
                    </section>  {/* End of Content Grid */}

                    {/* Recent Expenses */}

                    <section className="card recent-card">

                        <div className="card-heading">

                            <h2 className="card-title">
                                RECENT EXPENSES
                            </h2>

                            <span className="card-link">
                                View all expenses →
                            </span>

                        </div>

                        <table className="expense-table">

                            <thead>

                                <tr>
                                    <th>Description</th>
                                    <th>Category</th>
                                    <th>Payment Method</th>
                                    <th>Date</th>
                                    <th>Amount</th>
                                </tr>

                            </thead>

                            <tbody>

                                {dashboard.recentExpenses.map((expense) => (

                                    <tr key={expense.id}>

                                        <td className="expense-description">
                                            {expense.description}
                                        </td>

                                        <td>
                                            <span className="expense-category">
                                                {expense.category}
                                            </span>
                                        </td>

                                        <td>
                                            <span className="payment-mode">
                                                {expense.paymentMode}
                                            </span>
                                        </td>

                                        <td>
                                            {new Date(
                                                expense.date + "T00:00:00"
                                            ).toLocaleDateString(
                                                "en-IN",
                                                {
                                                    day: "2-digit",
                                                    month: "short",
                                                    year: "numeric"
                                                }
                                            )}
                                        </td>

                                        <td className="expense-amount">
                                            {formatCurrency(expense.amount)}
                                        </td>
                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </section>

                </AppLayout>
    );
}

export default Dashboard;