import { useEffect, useState } from "react";

function ExpenseForm({
    expense,
    onSubmit,
    onClose,
    submitting,
    formError
}) {
    const [form, setForm] = useState({
        amount: "",
        category: "",
        description: "",
        paymentMode: "UPI",
        date: new Date().toISOString().split("T")[0]
    });

    useEffect(() => {
        if (expense) {
            setForm({
                amount: expense.amount,
                category: expense.category,
                description: expense.description,
                paymentMode: expense.paymentMode,
                date: expense.date
            });
        }
    }, [expense]);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        onSubmit({
            ...form,
            amount: form.amount
        });
    };

    const isEditing = Boolean(expense);

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div
                className="expense-modal"
                onClick={(event) => event.stopPropagation()}
            >
                <div className="modal-header">
                    <div>
                        <div className="eyebrow">
                            {isEditing
                                ? "EDIT TRANSACTION"
                                : "NEW TRANSACTION"}
                        </div>

                        <h2 className="modal-title">
                            {isEditing
                                ? "Edit Expense"
                                : "Add Expense"}
                        </h2>
                    </div>

                    <button
                        type="button"
                        className="modal-close"
                        onClick={onClose}
                        disabled={submitting}
                    >
                        ×
                    </button>
                </div>

                {formError && (
                    <p className="form-error">
                        {formError}
                    </p>
                )}

                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label htmlFor="amount">
                            Amount
                        </label>

                        <div className="amount-input">
                            <span>₹</span>

                            <input
                                id="amount"
                                name="amount"
                                type="number"
                                min="0.01"
                                step="0.01"
                                value={form.amount}
                                onChange={handleChange}
                                placeholder="0.00"
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="category">
                            Category
                        </label>

                        <input
                            id="category"
                            name="category"
                            type="text"
                            value={form.category}
                            onChange={handleChange}
                            placeholder="e.g. Food"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="description">
                            Description
                        </label>

                        <input
                            id="description"
                            name="description"
                            type="text"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="What did you spend on?"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="paymentMode">
                            Payment Mode
                        </label>

                        <select
                            id="paymentMode"
                            name="paymentMode"
                            value={form.paymentMode}
                            onChange={handleChange}
                            required
                        >
                            <option value="UPI">UPI</option>
                            <option value="CASH">Cash</option>
                            <option value="CARD">Card</option>
                            <option value="NET_BANKING">
                                Net Banking
                            </option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="date">
                            Date
                        </label>

                        <input
                            id="date"
                            name="date"
                            type="date"
                            value={form.date}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="modal-actions">

                        <button
                            type="button"
                            className="secondary-button"
                            onClick={onClose}
                            disabled={submitting}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="primary-button"
                            disabled={submitting}
                        >
                            {submitting
                                ? isEditing
                                    ? "Saving..."
                                    : "Adding..."
                                : isEditing
                                    ? "Save Changes"
                                    : "Add Expense"}
                        </button>

                    </div>

                </form>
            </div>
        </div>
    );
}

export default ExpenseForm;