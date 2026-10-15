import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Tooltip,
    Filler,
} from "chart.js";

import { Line } from "react-chartjs-2";
import { formatCurrency } from "../utils/currency";

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Tooltip,
    Filler
);

function DailySpendingChart({ expenses, month }) {

    const [year, monthNumber] = month.split("-").map(Number);

    const daysInMonth = new Date(
        year,
        monthNumber,
        0
    ).getDate();

    const dailyTotals = Array.from(
        { length: daysInMonth },
        () => 0
    );

    expenses.forEach((expense) => {
        const day = Number(expense.date.slice(8, 10));

        dailyTotals[day - 1] += Number(expense.amount);
    });

    const labels = dailyTotals.map((_, index) => index + 1);

    const data = {
        labels,
        datasets: [
            {
                label: "Daily Spending",
                data: dailyTotals,
                borderColor: "#42dca2",
                backgroundColor: "rgba(66, 220, 162, 0.08)",
                borderWidth: 2,
                pointRadius: 2,
                pointHoverRadius: 5,
                pointBackgroundColor: "#42dca2",
                tension: 0.35,
                fill: true,
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
                    autoSkip: true,
                    maxTicksLimit: 8,
                },

                title: {
                    display: true,
                    text: "Day",
                    color: "#858b94",
                },
            },

            y: {
                beginAtZero: true,

                grid: {
                    color: "#1a1f23",
                },

                ticks: {
                    color: "#737981",

                    callback: (value) =>formatCurrency(value),
                },
            },
        },
    };

    return (
        <div className="analytics-chart">
            <Line data={data} options={options} />
        </div>
    );
}

export default DailySpendingChart;
