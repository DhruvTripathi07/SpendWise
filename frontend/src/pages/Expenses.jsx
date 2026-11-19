import { useCallback, useEffect, useState } from "react";
import api from "../services/api";
import AppLayout from "../components/AppLayout";
import ExpenseFilters from "../components/expenses/ExpenseFilters";
import ExpenseForm from "../components/expenses/ExpenseForm";
import { formatCurrency } from "../utils/currency";


function Expenses() {
    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);
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
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

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

            params.page = page;
            params.size = 20;

            const response = await api.get("/expenses", {
                params,
            });

            const lastPage = Math.max(response.data.totalPages - 1, 0);
            if (page > lastPage) {
                setPage(lastPage);
                return;
            }

            setExpenses(response.data.content);
            setTotalPages(response.data.totalPages);
            setTotalElements(response.data.totalElements);
        } catch (err) {
            console.error(err);
            setError("Failed to load expenses.");
        } finally {
            setLoading(false);
        }
    }, [month, category, page]);

    useEffect(() => {
        fetchExpenses();
    }, [fetchExpenses]);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const params = month ? { month } : {};
                const response = await api.get("/expenses/categories", { params });
                setCategories(response.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load expense categories.");
            }
        };

        fetchCategories();
    }, [month]);

    const resetFilters = () => {
        setMonth(getCurrentMonth());
        setCategory("");
        setPage(0);
    };

    return (
        <AppLayout>
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

                        <div className="month-controls">
                            <button
                                type="button"
                                className="month-button"
                                onClick={() => {
                                    const [year, currentMonth] = month.split("-");
                                    const date = new Date(
                                        Number(year),
                                        Number(currentMonth) - 2
                                    );

                                    setMonth(
                                        `${date.getFullYear()}-${String(
                                            date.getMonth() + 1
                                        ).padStart(2, "0")}`
                                    );
                                }}
                                aria-label="Previous month"
                            >
                                ‹
                            </button>

                            <div className="month-display">
                                {new Date(`${month}-01T12:00:00`).toLocaleDateString(
                                    "en-IN",
                                    { month: "long", year: "numeric" }
                                )}
                            </div>

                            <button
                                type="button"
                                className="month-button"
                                onClick={() => {
                                    const [year, currentMonth] = month.split("-");
                                    const date = new Date(
                                        Number(year),
                                        Number(currentMonth)
                                    );

                                    setMonth(
                                        `${date.getFullYear()}-${String(
                                            date.getMonth() + 1
                                        ).padStart(2, "0")}`
                                    );
                                }}
                                aria-label="Next month"
                            >
                                ›
                            </button>

                            <button
                                type="button"
                                className="month-button"
                                onClick={() => {
                                    setFormError("");
                                    setShowForm(true);
                                }}
                            >
                                + Add Expense
                            </button>
                        </div>


                    </header>

                    <section className="card recent-card">

                        <div className="card-heading">

                            <h2 className="card-title">
                                EXPENSES
                            </h2>

                            <span className="card-link">
                                {totalElements} expenses
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

                        {!loading && !error && totalPages > 1 && (
                            <div className="pagination-controls">
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={() => setPage((currentPage) => currentPage - 1)}
                                    disabled={page === 0}
                                >
                                    Previous
                                </button>
                                <span>
                                    Page {page + 1} of {totalPages}
                                </span>
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={() => setPage((currentPage) => currentPage + 1)}
                                    disabled={page + 1 >= totalPages}
                                >
                                    Next
                                </button>
                            </div>
                        )}

                    </section>
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
        </AppLayout>
    );
}

export default Expenses;