import { useEffect, useState } from "react";
import { getCurrency } from "../../utils/currency";

function BudgetForm({
    budget,
    selectedMonth,
    onSubmit,
    onClose,
    submitting,
    formError
}) {
    const [form, setForm] = useState({
        amount: "",
        category: "",
        month: selectedMonth || new Date().toISOString().slice(0, 7)
    });

    useEffect(() => {
        if (budget) {
            setForm({
                amount: budget.amount,
                category: budget.category,
                month: budget.month.slice(0, 7)
            });
        }
    }, [budget]);

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
            amount: form.amount,
            month: `${form.month}-01`
        });
    };

    const isEditing = Boolean(budget);

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
                                ? "EDIT BUDGET"
                                : "NEW BUDGET"}
                        </div>

                        <h2 className="modal-title">
                            {isEditing
                                ? "Edit Budget"
                                : "Add Budget"}
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
                        <label htmlFor="budget-category">
                            Category
                        </label>

                        <input
                            id="budget-category"
                            name="category"
                            type="text"
                            value={form.category}
                            onChange={handleChange}
                            placeholder="e.g. Food"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="budget-amount">
                            Budget Amount
                        </label>

                        <div className="amount-input">
                            <span>{getCurrency().symbol}</span>

                            <input
                                id="budget-amount"
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
                        <label htmlFor="budget-month">
                            Month
                        </label>

                        <input
                            id="budget-month"
                            name="month"
                            type="month"
                            value={form.month}
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
                                    : "Add Budget"}
                        </button>

                    </div>

                </form>
            </div>
        </div>
    );
}

export default BudgetForm;