import { Navigate } from "react-router-dom";
import { useSession } from "../context/SessionContext";

/** Redirige a /login si no hay sesión activa (modo simulado o MSAL). */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated, inProgress } = useSession();

  // Mientras MSAL termina de procesar la redirección de vuelta desde Microsoft, no expulsamos al usuario.
  if (inProgress) {
    return (
      <div className="login-page">
        <div className="card login-card"><p>Completando inicio de sesión…</p></div>
      </div>
    );
  }

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return children;
}
