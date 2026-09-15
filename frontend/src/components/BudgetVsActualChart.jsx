import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Tooltip,
} from "chart.js";

import { Bar } from "react-chartjs-2";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Tooltip
);

function BudgetVsActualChart({ budgets }) {

    const labels = budgets.map(
        (budget) => budget.category
    );

    const budgetValues = budgets.map(
        (budget) => Number(budget.amount)
    );

    const actualValues = budgets.map(
        (budget) => Number(budget.spent)
    );

    const data = {
        labels,

        datasets: [
            {
                label: "Budget",
                data: budgetValues,
                backgroundColor: "#4c9ac5",
                borderRadius: 6,
                maxBarThickness: 32,
            },

            {
                label: "Actual",
                data: actualValues,
                backgroundColor: "#42dca2",
                borderRadius: 6,
                maxBarThickness: 32,
            },
        ],
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,

        plugins: {
            legend: {
                position: "top",

                labels: {
                    color: "#cbd0d5",
                    usePointStyle: true,
                    pointStyle: "circle",
                    padding: 18,
                },
            },

            tooltip: {
                callbacks: {
                    label: (context) =>
                        ` ${context.dataset.label}: ₹${Number(
                            context.parsed.y
                        ).toLocaleString("en-IN")}`,
                },
            },
        },

        scales: {
            x: {
                grid: {
                    display: false,
                },

                ticks: {
                    color: "#737981",
                },
            },

            y: {
                beginAtZero: true,

                grid: {
                    color: "#1a1f23",
                },

                ticks: {
                    color: "#737981",

                    callback: (value) =>
                        `₹${Number(value).toLocaleString("en-IN")}`,
                },
            },
        },
    };

    return (
        <div className="analytics-budget-chart">
            <Bar
                data={data}
                options={options}
            />
        </div>
    );
}

export default BudgetVsActualChart;