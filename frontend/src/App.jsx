import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Login from "./pages/Login";
import Expenses from "./pages/Expenses";
import Budgets from "./pages/Budgets";
import Analytics from "./pages/Analytics";
import Signup from "./pages/Signup";
import Settings from "./pages/Settings";

function App() {
    const token = localStorage.getItem("token");

    return (
        <BrowserRouter>
            <Routes>
                <Route
                    path="/login"
                    element={token ? <Navigate to="/" replace /> : <Login />}
                />

                <Route
                    path="/signup"
                    element={token ? <Navigate to="/" replace /> : <Signup />}
                />

                <Route
                    path="/"
                    element={
                        token ? <Dashboard /> : <Navigate to="/login" replace />
                    }
                />

                <Route
                    path="/expenses"
                    element={
                        token ? <Expenses /> : <Navigate to="/login" replace />
                    }
                />

                <Route
                    path="/budgets"
                    element={
                        token ? <Budgets /> : <Navigate to="/login" replace />
                    }
                />

                <Route
                    path="/analytics"
                    element={
                        token ? <Analytics /> : <Navigate to="/login" replace />
                    }
                />

                <Route
                    path="/settings"
                    element={
                        token ? <Settings /> : <Navigate to="/login" replace />
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;