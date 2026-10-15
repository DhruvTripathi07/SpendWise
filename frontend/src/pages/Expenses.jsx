import { useCallback, useEffect, useState } from "react";
import api from "../services/api";
import Sidebar from "../components/Sidebar";
import ExpenseFilters from "../components/expenses/ExpenseFilters";
import ExpenseForm from "../components/expenses/ExpenseForm";
import { formatCurrency } from "../utils/currency";
import "../App.css";

function Expenses() {
    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [formError, setFormError] = useState("");
    const [editingExpense, setEditingExpense] = useState(null);
    const [deletingExpense, setDeletingExpense] = useState(null);
    const getCurrentMonth = () => {
        const now = new Date();

        return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
    };
    const [month, setMonth] = useState(getCurrentMonth());
    const [category, setCategory] = useState("");

    //Adding an Expense
    const handleAddExpense = async (expense) => {
        setSubmitting(true);
        setFormError("");

        try {
            await api.post("/expenses", expense);

            setShowForm(false);

            await fetchExpenses();
        } catch (err) {
            console.error(err);

            setFormError("Failed to add expense.");
        } finally {
            setSubmitting(false);
        }
    };

    //Editing an Expense
    const handleEditExpense = async (expense) => {
        setSubmitting(true);
        setFormError("");

        try {
            await api.put(`/expenses/${editingExpense.id}`, expense);

            setEditingExpense(null);

            await fetchExpenses();
        } catch (err) {
            console.error(err);

            setFormError("Failed to update expense.");
        } finally {
            setSubmitting(false);
        }
    };

    //Deleting an Expense
    const handleDeleteExpense = async () => {
        if (!deletingExpense) {
            return;
        }

        try {
            await api.delete(`/expenses/${deletingExpense.id}`);

            setDeletingExpense(null);

            await fetchExpenses();
        } catch (err) {
            console.error(err);

            setError("Failed to delete expense.");
            setDeletingExpense(null);
        }
    };

    const fetchExpenses = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const params = {};

            if (month) {
                params.month = month;
            }

            if (category) {
                params.category = category;
            }

            const response = await api.get("/expenses", {
                params,
            });

            setExpenses(response.data);
        } catch (err) {
            console.error(err);
            setError("Failed to load expenses.");
        } finally {
            setLoading(false);
        }
    }, [month, category]);

    useEffect(() => {
        fetchExpenses();
    }, [fetchExpenses]);

    const categories = [
        ...new Set(expenses.map((expense) => expense.category))
    ];

    const resetFilters = () => {
        setMonth(getCurrentMonth());
        setCategory("");
    };

    return (
        <div className="app">
            <div className="app-shell">

                <Sidebar />

                <main className="main-content">

                    <header className="dashboard-header">

                        <div>
                            <div className="eyebrow">
                                TRANSACTION HISTORY
                            </div>

                            <h1 className="dashboard-title">
                                Expenses
                            </h1>

                            <p className="dashboard-subtitle">
                                Track where your money is going
                            </p>
                        </div>

                        <button 
                            className="month-button" 
                            onClick={() => {
                                setFormError("");
                                setShowForm(true)
                                }}>
                            + Add Expense
                        </button>

                    </header>

                    <ExpenseFilters
                        month={month}
                        category={category}
                        categories={categories}
                        onMonthChange={setMonth}
                        onCategoryChange={setCategory}
                        onReset={resetFilters}
                    />

                    <section className="card recent-card">

                        <div className="card-heading">

                            <h2 className="card-title">
                                EXPENSES
                            </h2>

                            <span className="card-link">
                                {expenses.length} expenses
                            </span>

                        </div>

                        {loading && (
                            <p className="loading-state">
                                Loading expenses...
                            </p>
                        )}

                        {error && (
                            <p className="error-state">
                                {error}
                            </p>
                        )}

                        {!loading && !error && expenses.length === 0 && (
                            <p className="empty-state">
                                No expenses found for the selected filters.
                            </p>
                        )}

                        {!loading && !error && expenses.length > 0 && (
                            <table className="expense-table">

                                <thead>
                                    <tr>
                                        <th>Description</th>
                                        <th>Category</th>
                                        <th>Payment Method</th>
                                        <th>Date</th>
                                        <th>Amount</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {expenses.map((expense) => (
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

                                            <td className="expense-actions">
                                                <button
                                                    type="button"
                                                    className="table-action edit-action"
                                                    onClick={() => {
                                                        setFormError("");
                                                        setEditingExpense(expense);
                                                    }}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    type="button"
                                                    className="table-action delete-action"
                                                    onClick={() => setDeletingExpense(expense)}
                                                >
                                                    Delete
                                                </button>

                                            </td>    
                                        </tr>
                                    ))}
                                </tbody>

                            </table>
                        )}

                    </section>

                </main>

            </div>
            {(showForm || editingExpense) && (
                <ExpenseForm
                    expense={editingExpense}
                    onSubmit={
                        editingExpense
                            ? handleEditExpense
                            : handleAddExpense
                    }
                    onClose={() => {
                        if (!submitting) {
                            setShowForm(false);
                            setEditingExpense(null);
                            setFormError("");
                        }
                    }}
                    submitting={submitting}
                    formError={formError}
                />
            )}
            {deletingExpense && (
                <div
                    className="modal-overlay"
                    onClick={() => setDeletingExpense(null)}
                >
                    <div
                        className="expense-modal delete-modal"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="eyebrow">
                            DELETE TRANSACTION
                        </div>

                        <h2 className="modal-title">
                            Delete this expense?
                        </h2>

                        <p className="delete-message">
                            This will permanently remove{" "}
                            <strong>
                                {deletingExpense.description}
                            </strong>{" "}
                            from your expenses.
                        </p>

                        <div className="modal-actions">

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={() => setDeletingExpense(null)}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="delete-confirm-button"
                                onClick={handleDeleteExpense}
                            >
                                Delete
                            </button>

                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Expenses;