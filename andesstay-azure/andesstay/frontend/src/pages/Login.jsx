import { useState } from "react";
import { Navigate } from "react-router-dom";
import { useSession } from "../context/SessionContext";

// Login real: redirige a Microsoft (Azure AD) con MSAL.
export default function Login() {
  const { login, inProgress, isAuthenticated } = useSession();
  const [redirecting, setRedirecting] = useState(false);
  const [error, setError] = useState(null);

  // Si ya hay sesión, no tiene sentido quedarse en /login
  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  async function handleLogin() {
    setError(null);
    setRedirecting(true);
    try {
      await login();
    } catch (e) {
      console.error("[Login]", e);
      setError(e.errorCode || e.message);
      setRedirecting(false);
    }
  }

  return (
    <div className="login-page">
      <div className="card login-card">
        <div className="brand-mark" style={{ margin: "0 auto 1rem" }}>AS</div>
        <h1>AndesStay</h1>
        <p className="muted">Acceso corporativo para hostales, cabañas y lodges.</p>
        <button className="btn-primary" onClick={handleLogin} disabled={redirecting || inProgress}>
          {redirecting || inProgress ? "Redirigiendo…" : "Iniciar sesión con Microsoft"}
        </button>
        {error && <p style={{ color: "crimson", marginTop: "1rem" }}>Error: {error}</p>}
      </div>
    </div>
  );
}