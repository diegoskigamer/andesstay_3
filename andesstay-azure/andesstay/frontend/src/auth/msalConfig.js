import { PublicClientApplication, LogLevel } from "@azure/msal-browser";

// -----------------------------------------------------------------------
// Login directo con Azure AD (MSAL). Variables en frontend/.env:
//
//   VITE_AUTH_MODE=msal
//   VITE_AZURE_CLIENT_ID=<Application (client) ID de la App Registration>
//   VITE_AZURE_TENANT_ID=<Directory (tenant) ID>
//   VITE_AZURE_REDIRECT_URI=http://localhost:5173
//
// En Azure Portal → App registration → Authentication: agrega la
// plataforma "Single-page application" con Redirect URI http://localhost:5173
//
// Si VITE_AUTH_MODE no es "msal" (o faltan las variables), la app usa el
// modo simulado (selector de rol) sin romperse.
// -----------------------------------------------------------------------

export const AUTH_MODE = import.meta.env.VITE_AUTH_MODE || "simulated"; // "simulated" | "msal"

const clientId = import.meta.env.VITE_AZURE_CLIENT_ID;
const tenantId = import.meta.env.VITE_AZURE_TENANT_ID;
const redirectUri = import.meta.env.VITE_AZURE_REDIRECT_URI || window.location.origin;

export const isMsalConfigured = Boolean(clientId && tenantId);

export const msalConfig = {
  auth: {
    clientId: clientId || "MISSING_CLIENT_ID",
    authority: `https://login.microsoftonline.com/${tenantId || "MISSING_TENANT_ID"}/`,
    redirectUri,
    postLogoutRedirectUri: `${window.location.origin}/login`,
    navigateToLoginRequestUrl: false,
  },
  cache: {
    cacheLocation: "sessionStorage",
  },
  system: {
    loggerOptions: {
      loggerCallback: (level, message, containsPii) => {
        if (containsPii) return;
        if (level === LogLevel.Error) console.error("[MSAL]", message);
      },
      logLevel: LogLevel.Warning,
    },
  },
};

// Pide el id_token (trae el claim "roles" con los App Roles del usuario).
export const loginRequest = {
  scopes: ["openid", "profile", "email"],
  // "select_account" fuerza a Microsoft a preguntar con qué cuenta entrar,
  // así puedes cambiar de usuario sin quedar pegado a la sesión anterior.
  prompt: "select_account",
};

export const msalInstance = isMsalConfigured ? new PublicClientApplication(msalConfig) : null;
