import Sidebar from "./Sidebar";
import Footer from "./Footer";

function AppLayout({ children }) {
    return (
        <div className="app">
            <div className="app-shell">
                <Sidebar />

                <div className="app-content">
                    <main className="main-content">
                        {children}
                    </main>

                    <Footer />
                </div>
            </div>
        </div>
    );
}

export default AppLayout;