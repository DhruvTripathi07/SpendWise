import {useState, useEffect} from "react";
import {useLocation} from "react-router-dom";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

function AppLayout({ children }) {
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const location = useLocation();

    useEffect(() => { setSidebarOpen(false); }, [location.pathname]);

    useEffect(() => { 
        document.body.style.overflow = sidebarOpen ? "hidden" : ""; 
        return () => { document.body.style.overflow = ""; }; 
    }, [sidebarOpen]);
    
    return (
        <div className="app">
            <button 
                className={`mobile-menu-button ${sidebarOpen ? "menu-button-open" : ""}`} 
                onClick={() => setSidebarOpen(prev => !prev)} 
                aria-label={sidebarOpen ? "Close navigation menu" : "Open navigation menu"}
                aria-expanded={sidebarOpen} 
                aria-controls="app-sidebar" 
                > 
                <span className="menu-orbit">
                        <span className="menu-orbit-dot"></span> 
                </span> 
                
                <span></span> 
            </button>
            {sidebarOpen && ( 
                <button 
                    className="sidebar-backdrop" 
                    onClick={() => setSidebarOpen(false)} 
                    aria-label="Close navigation menu" 
                    /> 
                )}

            <div className="app-shell"> 
                <div 
                    id="app-sidebar" 
                    className={`sidebar-container ${
                         sidebarOpen ? "sidebar-container-open" : "" 
                    }`} > 
                    <Sidebar onNavigate={() => setSidebarOpen(false)} /> 
                </div> 
                
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