import Dashboard from "./pages/Dashboard";
import Login from "./pages/Login";

function App() {
    const token = localStorage.getItem("token");

    if (token) {
        return <Dashboard />;
    }

    return <Login />;
}

export default App;