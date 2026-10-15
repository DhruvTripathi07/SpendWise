import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import CURRENCIES, { getCurrency } from "../utils/currency";
import api from "../services/api";
import "../App.css";

function Settings() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [currency, setCurrency] = useState(getCurrency().code);

    const handleCurrencyChange = (event) => {
        const selectedCurrency = event.target.value;

        localStorage.setItem("currency", selectedCurrency);
        setCurrency(selectedCurrency);
    };

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get("/auth/me");
                setUser(response.data);
            } catch (error) {
                console.error("Failed to fetch user:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, []);
    return (
        <div className="app">
            <div className="app-shell">

                <Sidebar />

                <main className="main-content">

                    <header className="dashboard-header">
                        <div>
                            <h1 className="dashboard-title">
                                Settings
                            </h1>

                            <p className="dashboard-subtitle">
                                Manage your SpendWise preferences.
                            </p>
                        </div>
                    </header>
                    <section className="settings-section">
                        <div className="settings-section-heading">
                            <h2>Account</h2>
                            <p>Your SpendWise account information.</p>
                        </div>

                        <div className="settings-card">
                            {loading ? (
                                <p>Loading account information...</p>
                            ) : user ? (
                                <>
                                    <div className="settings-field">
                                        <label>Name</label>
                                        <p>{user.name}</p>
                                    </div>

                                    <div className="settings-field">
                                        <label>Email</label>
                                        <p>{user.email}</p>
                                    </div>
                                </>
                            ) : (
                                <p>Unable to load account information.</p>
                            )}
                        </div>
                    </section>
                    <section className="settings-section">
                        <div className="settings-card currency-card">
                            <div className="currency-description">
                                <h3>Display currency</h3>
                                <p>Choose how monetary amounts appear throughout SpendWise.</p>
                            </div>

                            <div className="currency-control">
                                <select
                                    id="currency"
                                    value={currency}
                                    onChange={handleCurrencyChange}
                                    className="settings-select"
                                >
                                    {Object.values(CURRENCIES).map((item) => (
                                        <option key={item.code} value={item.code}>
                                            {item.label}
                                        </option>
                                    ))}
                                </select>

                                <span className="currency-current">
                                    Currently selected: {currency}
                                </span>
                            </div>
                        </div>
                    </section>
                </main>
            </div>
        </div>
    );
}

export default Settings;