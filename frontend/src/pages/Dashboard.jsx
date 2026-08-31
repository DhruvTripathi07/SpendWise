import { useEffect, useState } from "react";
import api from "../services/api";
import Sidebar from "../components/Sidebar";
import "../App.css";

function Dashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchDashboard = async () => {
            try {
                const response = await api.get("/dashboard?month=2026-08");
                setDashboard(response.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load dashboard.");
            } finally {
                setLoading(false);
            }
        };

        fetchDashboard();
    }, []);

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

    return (
        <div className="app">
            <div className="app-shell">

                <Sidebar />

                <main className="main-content">

                    {/* Dashboard Header */}

                    <header className="dashboard-header">

                        <div>
                            <div className="eyebrow">
                                FINANCIAL OVERVIEW
                            </div>

                            <h1 className="dashboard-title">
                                Welcome back, Test User 2 👋
                            </h1>

                            <p className="dashboard-subtitle">
                                Here's your financial overview
                            </p>
                        </div>

                        <div className="month-controls">

                            <button className="month-button">
                                ‹
                            </button>

                            <div className="month-display">
                                ◫ August 2026
                            </div>

                            <button className="month-button">
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
                                ₹{totalSpent.toLocaleString("en-IN")}
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
                                ₹{totalBudget.toLocaleString("en-IN")}
                            </div>

                            <div className="card-caption">
                                for August 2026
                            </div>
                        </div>

                        <div className="card summary-card">
                            <div className="card-label">
                                AMOUNT LEFT
                            </div>

                            <div className="money accent">
                                ₹{remaining.toLocaleString("en-IN")}
                            </div>

                            <div className="card-caption">
                                remaining budget
                            </div>
                        </div>

                    </section>

                    {/* Category + Budget */}

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
                                                ₹{totalSpent.toLocaleString("en-IN")}
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
                                                        ₹{Number(amount).toLocaleString("en-IN")}
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
                                                    ₹{Number(budget.spent).toLocaleString("en-IN")}
                                                    {" / "}
                                                    ₹{Number(budget.amount).toLocaleString("en-IN")}
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
                                                    ₹{Number(
                                                        budget.remaining
                                                    ).toLocaleString("en-IN")}{" "}
                                                    remaining
                                                </span>

                                                <span className="budget-percent">
                                                    {Number(
                                                        budget.progress ??
                                                        0
                                                    ).toFixed(2)}%
                                                </span>

                                            </div>

                                        </div>
                                    );
                                })}

                            </div>

                        </div>

                    </section>

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
                                            ₹{Number(
                                                expense.amount
                                            ).toLocaleString("en-IN")}
                                        </td>

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </section>

                </main>

            </div>
        </div>
    );
}

export default Dashboard;