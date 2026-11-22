import { MsalProvider, useMsal, useIsAuthenticated } from "@azure/msal-react";
import { InteractionStatus } from "@azure/msal-browser";
import { SessionContext, ROLES, AZURE_APP_ROLE_TO_ROLE } from "../context/SessionContext";
import { msalInstance, loginRequest } from "./msalConfig";

// MODO MSAL: el rol se lee del claim "roles" del id_token de Azure AD
// (App Roles asignados al usuario en Enterprise Applications → Users and groups).

function rolesFromClaims(claims) {
  const raw = claims?.roles || [];
  return raw.map((r) => AZURE_APP_ROLE_TO_ROLE[r]).filter(Boolean);
}

function MsalSessionBridge({ children }) {
  const { instance, accounts, inProgress } = useMsal();
  const isAuthenticated = useIsAuthenticated();
  const account = accounts[0];

  const mapped = rolesFromClaims(account?.idTokenClaims);
  // Si el usuario tiene varios roles, se usa el de mayor jerarquía.
  const priority = [ROLES.ADMIN, ROLES.RECEPCIONISTA, ROLES.AUDITOR, ROLES.HUESPED];
  const role = priority.find((r) => mapped.includes(r)) || null;

  const value = {
    role,
    name: account?.name || account?.username || "",
    isAuthenticated,
    authMode: "msal",
    inProgress: inProgress !== InteractionStatus.None,
    login: () => instance.loginRedirect(loginRequest),
    logout: () => instance.logoutRedirect({ account }),
  };

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function MsalSessionProvider({ children }) {
  return (
    <MsalProvider instance={msalInstance}>
      <MsalSessionBridge>{children}</MsalSessionBridge>
    </MsalProvider>
  );
}
