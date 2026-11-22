import { createContext, useContext } from "react";

// Contexto único de sesión. Tanto el modo simulado (RoleContext) como el
// modo MSAL real (auth/MsalSessionProvider) escriben en este mismo
// contexto, así que el resto de la app no necesita saber cuál está activo.

export const ROLES = {
  ADMIN: "Admin",
  RECEPCIONISTA: "Recepcionista",
  HUESPED: "Huesped",
  AUDITOR: "Auditor",
};

// Nombre exacto (campo "Value") de cada App Role creado en Azure AD
// (App Registration → App roles) → rol interno de la app.
export const AZURE_APP_ROLE_TO_ROLE = {
  Admin: ROLES.ADMIN,
  admin: ROLES.ADMIN,
  Recepcionista: ROLES.RECEPCIONISTA,
  Operador: ROLES.RECEPCIONISTA,
  Huesped: ROLES.HUESPED,
  Cliente: ROLES.HUESPED,
  Auditor: ROLES.AUDITOR,
};

export const SessionContext = createContext(null);

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error("useSession debe usarse dentro de un SessionProvider");
  return ctx;
}
