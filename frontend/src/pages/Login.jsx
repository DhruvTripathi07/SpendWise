import { useState } from "react";
import api from "../services/api";
import "../App.css";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (event) => {
        event.preventDefault();
        setError("");

        try {
            const response = await api.post("/auth/login", {
                email,
                password,
            });

            localStorage.setItem("token", response.data.token);

            window.location.href = "/";
        } catch (err) {
            console.error(err);
            setError("Invalid email or password.");
        }
    };

    return (
        <div className="login-page">

            {/* Login content */}
            <main className="login-content">

                {/* Brand */}
                <div className="login-brand">

                    <div className="login-logo">
                        <svg
                            viewBox="0 0 64 44"
                            aria-hidden="true"
                        >
                            <path
                                d="M4 22
                                C15 7, 28 3, 32 3
                                C36 3, 49 7, 60 22
                                C49 37, 36 41, 32 41
                                C28 41, 15 37, 4 22Z"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2.2"
                            />

                            <rect
                                x="23"
                                y="21"
                                width="4"
                                height="9"
                                rx="1"
                                fill="currentColor"
                            />

                            <rect
                                x="30"
                                y="16"
                                width="4"
                                height="14"
                                rx="1"
                                fill="currentColor"
                            />

                            <rect
                                x="37"
                                y="11"
                                width="4"
                                height="19"
                                rx="1"
                                fill="currentColor"
                            />
                        </svg>
                    </div>

                    <h1>SpendWise</h1>

                    <p>Your money. Your picture.</p>
                </div>

                {/* Login card */}
                <section className="login-card">

                    <div className="login-heading">
                        <h2>Welcome back</h2>

                        <p>
                            Sign in to continue to your financial overview.
                        </p>
                    </div>

                    <form onSubmit={handleLogin}>

                        <div className="login-field">
                            <label htmlFor="email">
                                Email
                            </label>

                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(event) =>
                                    setEmail(event.target.value)
                                }
                                placeholder="you@example.com"
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="login-field">
                            <label htmlFor="password">
                                Password
                            </label>

                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                placeholder="Enter your password"
                                autoComplete="current-password"
                                required
                            />
                        </div>

                        {error && (
                            <p className="login-error">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            className="login-button"
                        >
                            Login
                        </button>

                        <p className="login-signup">
                            Don't have an account? 
                            <button
                                type="button"
                                onClick={() => {
                                    window.location.href = "/signup";
                                }}
                            >
                                Sign Up
                            </button>
                        </p>
                    </form>

                </section>

            </main>
        </div>
    );
}

export default Login;