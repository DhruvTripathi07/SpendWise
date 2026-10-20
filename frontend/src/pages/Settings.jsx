import { useEffect, useState } from "react";
import AppLayout from "../components/AppLayout";
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
        <AppLayout>
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
                    <section className="settings-section">
                        <div className="settings-section-heading">
                            <h2>Under Development</h2>
                            <p>More ways to manage and personalize your finances are on the way.</p>
                        </div>

                        <div className="settings-card development-card">
                            <div className="development-item">
                                <span>Email verification</span>
                                <span className="development-badge">Planned</span>
                            </div>

                            <div className="development-item">
                                <span>Password recovery</span>
                                <span className="development-badge">Planned</span>
                            </div>

                            <div className="development-item">
                                <span>Advanced account management</span>
                                <span className="development-badge">Planned</span>
                            </div>

                            <div className="development-item">
                                <span>Notifications</span>
                                <span className="development-badge">Planned</span>
                            </div>

                            <div className="development-item">
                                <span>More personalization</span>
                                <span className="development-badge">Planned</span>
                            </div>
                        </div>
                    </section>
        </AppLayout>
    );
}

export default Settings;