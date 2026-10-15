import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Tooltip,
} from "chart.js";

import { Bar } from "react-chartjs-2";
import { formatCurrency } from "../utils/currency";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Tooltip
);

function SixMonthSpendingChart({ monthlySpending }) {

    const labels = monthlySpending.map((item) => {
        const [year, month] = item.month.split("-");

        return new Date(
            Number(year),
            Number(month) - 1,
            1
        ).toLocaleDateString("en-IN", {
            month: "short",
            year: "numeric",
        });
    });

    const values = monthlySpending.map(
        (item) => Number(item.totalSpent)
    );

    const data = {
        labels,
        datasets: [
            {
                label: "Spending",
                data: values,
                backgroundColor: "#42dca2",
                borderRadius: 6,
                maxBarThickness: 42,
            },
        ],
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,

        plugins: {
            legend: {
                display: false,
            },

            tooltip: {
                callbacks: {
                    label: (context) =>formatCurrency(context.parsed.y),
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

                    callback: (value) => formatCurrency(value),
                },
            },
        },
    };

    return (
        <div className="analytics-bar-chart">
            <Bar
                data={data}
                options={options}
            />
        </div>
    );
}

export default SixMonthSpendingChart;