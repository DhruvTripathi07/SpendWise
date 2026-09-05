import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend,
} from "chart.js";

import { Doughnut } from "react-chartjs-2";

ChartJS.register(
    ArcElement,
    Tooltip,
    Legend
);

function CategorySpendingChart({ categories }) {

    const labels = Object.keys(categories || {});
    const values = Object.values(categories || {}).map(Number);

    const data = {
        labels,
        datasets: [
            {
                data: values,
                backgroundColor: [
                    "#9aa19e",
                    "#7560c8",
                    "#4c9ac5",
                    "#b38e43",
                    "#42dca2",
                    "#a95c6c",
                ],
                borderColor: "#0d1013",
                borderWidth: 3,
            },
        ],
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,

        cutout: "68%",

        plugins: {
            legend: {
                position: "right",

                labels: {
                    color: "#cbd0d5",
                    padding: 16,
                    usePointStyle: true,
                    pointStyle: "circle",
                },
            },

            tooltip: {
                callbacks: {
                    label: (context) => {
                        const value = Number(
                            context.raw
                        ).toLocaleString("en-IN");

                        return ` ₹${value}`;
                    },
                },
            },
        },
    };

    return (
        <div className="analytics-donut">
            <Doughnut
                data={data}
                options={options}
            />
        </div>
    );
}

export default CategorySpendingChart;