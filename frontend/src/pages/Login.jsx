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

            {/* Animated financial background */}
            <div className="login-background" aria-hidden="true">
                <svg
                    className="login-trajectory"
                    viewBox="0 0 1440 900"
                    preserveAspectRatio="none"
                >
                    <defs>
                        <marker
                            id="green-arrow"
                            markerWidth="10"
                            markerHeight="10"
                            refX="8"
                            refY="5"
                            orient="auto"
                        >
                            <path
                                d="M 0 0 L 10 5 L 0 10 z"
                                fill="currentColor"
                            />
                        </marker>

                        <marker
                            id="purple-arrow"
                            markerWidth="10"
                            markerHeight="10"
                            refX="8"
                            refY="5"
                            orient="auto"
                        >
                            <path
                                d="M 0 0 L 10 5 L 0 10 z"
                                fill="currentColor"
                            />
                        </marker>
                    </defs>

                    {/* Main green trajectory */}
                    <path
                        className="trajectory trajectory-green"
                        d="M -80 650
                        L 170 610
                        L 290 690
                        L 470 430
                        L 650 500
                        L 820 670
                        L 1010 520
                        L 1190 330
                        L 1520 120"
                        markerEnd="url(#green-arrow)"
                    />

                    {/* Secondary silver trajectory */}
                    <path
                        className="trajectory trajectory-silver"
                        d="M -100 260
                        L 150 300
                        L 310 220
                        L 500 330
                        L 690 250
                        L 880 390
                        L 1080 270
                        L 1510 340"
                    />

                    {/* Subtle purple trajectory */}
                    <path
                        className="trajectory trajectory-purple"
                        d="M -100 760
                        L 220 720
                        L 400 790
                        L 610 650
                        L 820 710
                        L 1040 600
                        L 1250 680
                        L 1510 540"
                        markerEnd="url(#purple-arrow)"
                    />
                </svg>
            </div>

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
                            <span>→</span>
                        </button>

                    </form>

                </section>

            </main>
        </div>
    );
}

export default Login;