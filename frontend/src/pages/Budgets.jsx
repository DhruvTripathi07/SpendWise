import { useEffect, useState } from "react";
import api from "../services/api";
import AppLayout from "../components/AppLayout";
import BudgetForm from "../components/budgets/BudgetForm";
import { formatCurrency } from "../utils/currency";
import "../App.css";

function Budgets() {
    const [budgets, setBudgets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [formError, setFormError] = useState("");
    const [editingBudget, setEditingBudget] = useState(null);
    const [deletingBudget, setDeletingBudget] = useState(null);

    const fetchBudgets = async () => {
        setLoading(true);
        setError("");

        try {
            const response = await api.get("/budgets");
            setBudgets(response.data);
        } catch (err) {
            console.error(err);
            setError("Failed to load budgets.");
        } finally {
            setLoading(false);
        }
    };

    //Adding a new budget
    const handleAddBudget = async (budget) => {
        setSubmitting(true);
        setFormError("");

        try {
            await api.post("/budgets", budget);

            setShowForm(false);

            await fetchBudgets();
        } catch (err) {
            console.error("ADD BUDGET ERROR:", err);
            console.error("STATUS:", err.response?.status);
            console.error("RESPONSE:", err.response?.data);

            setFormError(
                err.response?.data?.message ||
                err.response?.data ||
                "Failed to add budget."
            );
        } finally {
            setSubmitting(false);
        }
    };

    //Updating an existing budget
    const handleEditBudget = async (budget) => {
        setSubmitting(true);
        setFormError("");

        try {
            await api.put(`/budgets/${editingBudget.id}`, budget);

            setEditingBudget(null);

            await fetchBudgets();
        } catch (err) {
            console.error(err);

            setFormError("Failed to update budget.");
        } finally {
            setSubmitting(false);
        }
    };

    //Deleting a budget
    const handleDeleteBudget = async () => {
        if (!deletingBudget) {
            return;
        }

        try {
            await api.delete(`/budgets/${deletingBudget.id}`);

            setDeletingBudget(null);

            await fetchBudgets();
        } catch (err) {
            console.error(err);

            setError("Failed to delete budget.");
            setDeletingBudget(null);
        }
    };

    useEffect(() => {
        fetchBudgets();
    }, []);

    return (
        <AppLayout>
                    <header className="dashboard-header">

                        <div>
                            <div className="eyebrow">
                                BUDGET MANAGEMENT
                            </div>

                            <h1 className="dashboard-title">
                                Budgets
                            </h1>

                            <p className="dashboard-subtitle">
                                Set limits and keep your spending on track
                            </p>
                        </div>

                        <button 
                        className="month-button"
                        onClick={() => {
                            setFormError("");
                            setShowForm(true)}}>
                            + Add Budget
                        </button>

                    </header>

                    <section className="card recent-card">

                        <div className="card-heading">

                            <h2 className="card-title">
                                YOUR BUDGETS
                            </h2>

                            <span className="card-link">
                                {budgets.length} budgets
                            </span>

                        </div>

                        {loading && (
                            <p className="loading-state">
                                Loading budgets...
                            </p>
                        )}

                        {error && (
                            <p className="error-state">
                                {error}
                            </p>
                        )}

                        {!loading && !error && budgets.length === 0 && (
                            <p className="empty-state">
                                No budgets found.
                            </p>
                        )}

                        {!loading && !error && budgets.length > 0 && (
                            <table className="expense-table">

                                <thead>
                                    <tr>
                                        <th>Category</th>
                                        <th>Month</th>
                                        <th>Budget</th>
                                        <th>Spent</th>
                                        <th>Remaining</th>
                                        <th>Progress</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>

                                <tbody>

                                    {budgets.map((budget) => {
                                        const actualProgress = Number(budget.progress ?? 0);
                                        const progressBar = Number(budget.progressPercent ?? 0);
                                        const overBudget = budget.overBudget === true;

                                        return (
                                            <tr key={budget.id}>

                                                <td className="expense-description">
                                                    {budget.category}
                                                </td>

                                                <td>
                                                    {budget.month}
                                                </td>

                                                <td>
                                                    {formatCurrency(budget.amount)}
                                                </td>

                                                <td>
                                                    {formatCurrency(budget.spent)}
                                                </td>

                                                <td>
                                                    {formatCurrency(budget.remaining)}
                                                </td>

                                                <td>
                                                    <div className="budget-progress-cell">

                                                        <div className="progress-track">
                                                            <div
                                                                className={`progress-bar ${
                                                                    overBudget ? "over-budget" : ""
                                                                }`}
                                                                style={{
                                                                    width: `${Math.min(progressBar, 100)}%`
                                                                }}
                                                            />
                                                        </div>

                                                        <span
                                                            className={
                                                                overBudget
                                                                    ? "delete-action"
                                                                    : "budget-percent"
                                                            }
                                                        >
                                                            {actualProgress.toFixed(2)}%
                                                        </span>

                                                    </div>
                                                </td>

                                                <td className="expense-actions">

                                                    <button
                                                        type="button"
                                                        className="table-action edit-action"
                                                        onClick={() => {
                                                            setFormError("");
                                                            setEditingBudget(budget);
                                                        }}
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="table-action delete-action"
                                                        onClick={() => setDeletingBudget(budget)}
                                                    >
                                                        Delete
                                                    </button>

                                                </td>

                                            </tr>
                                        );
                                    })}

                                </tbody>

                            </table>
                        )}

                    </section>
            {(showForm || editingBudget) && (
                <BudgetForm
                    budget={editingBudget}
                    onSubmit={
                        editingBudget
                            ? handleEditBudget
                            : handleAddBudget
                    }
                    onClose={() => {
                        if (!submitting) {
                            setShowForm(false);
                            setEditingBudget(null);
                            setFormError("");
                        }
                    }}
                    submitting={submitting}
                    formError={formError}
                />
            )}

            {deletingBudget && (
                <div
                    className="modal-overlay"
                    onClick={() => setDeletingBudget(null)}
                >
                    <div
                        className="expense-modal delete-modal"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="eyebrow">
                            DELETE BUDGET
                        </div>

                        <h2 className="modal-title">
                            Delete this budget?
                        </h2>

                        <p className="delete-message">
                            This will permanently remove the{" "}
                            <strong>
                                {deletingBudget.category}
                            </strong>{" "}
                            budget for{" "}
                            <strong>
                                {deletingBudget.month.slice(0, 7)}
                            </strong>.
                        </p>

                        <div className="modal-actions">

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={() => setDeletingBudget(null)}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="delete-confirm-button"
                                onClick={handleDeleteBudget}
                            >
                                Delete
                            </button>

                        </div>
                    </div>
                </div>
            )}
        </AppLayout>
    );
}

export default Budgets;