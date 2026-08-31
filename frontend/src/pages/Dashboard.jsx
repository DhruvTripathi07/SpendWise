import { useEffect, useState } from "react";
import api from "../services/api";

function Dashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchDashboard = async () => {
            try {
                const response = await api.get("/dashboard?month=2026-08");
                setDashboard(response.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load dashboard.");
            } finally {
                setLoading(false);
            }
        };

        fetchDashboard();
    }, []);

    if (loading) {
        return <p>Loading dashboard...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div>
            <h1>SpendWise Dashboard</h1>

            <h2>August 2026</h2>

            <p>
                Total Spent: ₹{dashboard.totalSpent}
            </p>

            <h3>Categories</h3>

            {Object.entries(dashboard.categories).map(([category, amount]) => (
                <p key={category}>
                    {category}: ₹{amount}
                </p>
            ))}

            <h3>Recent Expenses</h3>

            {dashboard.recentExpenses.map((expense) => (
                <p key={expense.id}>
                    {expense.description} — ₹{expense.amount}
                </p>
            ))}
        </div>
    );
}

export default Dashboard;