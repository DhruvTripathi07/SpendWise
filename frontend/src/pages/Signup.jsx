import { useState } from "react";
import api from "../services/api";
import "../App.css";

function Signup() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");

    const handleSignup = async (event) => {
        event.preventDefault();

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        try {
            const response = await api.post("/auth/register", {
                name,
                email,
                password
            });

            if (response.status === 201) {
                window.location.href = "/login";
            }
        } catch (error) {
            setError("Failed to create account");
        }
    };

    return (
        <div className="login-page">
            <main className="login-content">
                <div className="login-brand">
                    <div className="login-logo">
                        <svg viewBox="0 0 64 44" aria-hidden="true">
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
                            <rect x="23" y="21" width="4" height="9" rx="1" fill="currentColor" />
                            <rect x="30" y="16" width="4" height="14" rx="1" fill="currentColor" />
                            <rect x="37" y="11" width="4" height="19" rx="1" fill="currentColor" />
                        </svg>
                    </div>

                    <h1>SpendWise</h1>
                    <p>Your money. Your picture.</p>
                </div>

                <section className="login-card">
                    <div className="login-heading">
                        <h2>Create your account</h2>
                        <p>
                            Start managing your finances with SpendWise.
                        </p>
                    </div>

                    <form onSubmit={handleSignup}>
                        <div className="login-field">
                            <label htmlFor="name">Name</label>
                            <input
                                id="name"
                                type="text"
                                value={name}
                                onChange={(event) => setName(event.target.value)}
                                placeholder="Your name"
                                autoComplete="name"
                                required
                            />
                        </div>

                        <div className="login-field">
                            <label htmlFor="email">Email</label>
                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(event) => setEmail(event.target.value)}
                                placeholder="you@example.com"
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="login-field">
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                placeholder="At least 8 characters"
                                autoComplete="new-password"
                                required
                            />
                        </div>

                        <div className="login-field">
                            <label htmlFor="confirm-password">Confirm Password</label>
                            <input
                                id="confirm-password"
                                type="password"
                                value={confirmPassword}
                                onChange={(event) => setConfirmPassword(event.target.value)}
                                placeholder="Re-enter your password"
                                autoComplete="new-password"
                                required
                            />
                        </div>

                        {error && (
                            <p className="login-error">
                                {error}
                            </p>
                        )}
                        <button type="submit" className="login-button">
                            Sign Up
                        </button>

                        <p className="login-signup">
                            Already have an account?{" "}
                            <button
                                type="button"
                                onClick={() => {
                                    window.location.href = "/login";
                                }}
                            >
                                Log In
                            </button>
                        </p>
                    </form>
                </section>
            </main>
        </div>
    );
}

export default Signup;