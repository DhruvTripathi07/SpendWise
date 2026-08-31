function ExpenseFilters({
    month,
    category,
    categories,
    onMonthChange,
    onCategoryChange,
    onReset
}) {
    return (
        <div className="expense-filters">

            <div className="filter-group">
                <label htmlFor="expense-month">
                    Month
                </label>

                <input
                    id="expense-month"
                    type="month"
                    value={month}
                    onChange={(event) => onMonthChange(event.target.value)}
                />
            </div>

            <div className="filter-group">
                <label htmlFor="expense-category">
                    Category
                </label>

                <select
                    id="expense-category"
                    value={category}
                    onChange={(event) =>
                        onCategoryChange(event.target.value)
                    }
                >
                    <option value="">All categories</option>

                    {categories.map((item) => (
                        <option key={item} value={item}>
                            {item}
                        </option>
                    ))}
                </select>
            </div>

            <button
                type="button"
                className="filter-reset"
                onClick={onReset}
            >
                Reset
            </button>

        </div>
    );
}

export default ExpenseFilters;