import { Link, useLocation } from "react-router-dom";

function Sidebar() {
    const location = useLocation();

    const isActive = (path) => location.pathname === path;

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    };

    return (
        <aside className="sidebar">
            <div className="brand">
                <div className="brand-icon">S</div>

                <div>
                    <div className="brand-name">SpendWise</div>
                    <div className="brand-tagline">
                        Your money. Your picture.
                    </div>
                </div>
            </div>

            <nav className="nav">
                <Link
                    className={`nav-item ${isActive("/") ? "active" : ""}`}
                    to="/"
                >
                    <span className="nav-icon">▣</span>
                    Dashboard
                </Link>

                <Link
                    className={`nav-item ${
                        isActive("/expenses") ? "active" : ""
                    }`}
                    to="/expenses"
                >
                    <span className="nav-icon">▱</span>
                    Expenses
                </Link>

                <Link
                    className={`nav-item ${
                        isActive("/budgets") ? "active" : ""
                    }`}
                    to="/budgets"
                >
                    <span className="nav-icon">◷</span>
                    Budgets
                </Link>

                <Link
                    className={`nav-item ${
                        isActive("/analytics") ? "active" : ""
                    }`}
                    to="/analytics"
                >
                    <span className="nav-icon">⌁</span>
                    Analytics
                </Link>

                <a className="nav-item" href="#">
                    <span className="nav-icon">⚙</span>
                    Settings
                </a>
            </nav>

            <div className="sidebar-bottom">
                <div className="user-card">
                    <div className="user-info">
                        <div className="avatar">T2</div>

                        <div>
                            <div className="user-name">
                                Test User 2
                            </div>

                            <div className="user-email">
                                test2@spendwise.com
                            </div>
                        </div>
                    </div>

                    <button
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </div>
        </aside>
    );
}

export default Sidebar;