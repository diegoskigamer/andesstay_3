import { InteractionRequiredAuthError } from "@azure/msal-browser";
import { msalInstance } from "./msalConfig";

/**
 * Devuelve el id_token vigente (lo renueva en silencio si hace falta).
 * El BFF lo valida contra Azure AD y de ahí saca los roles.
 * Devuelve null en modo simulado o si no hay sesión.
 */
export async function getIdToken() {
  if (!msalInstance) return null;

  const account = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];
  if (!account) return null;

  try {
    const result = await msalInstance.acquireTokenSilent({
      scopes: ["openid", "profile", "email"],
      account,
    });
    return result.idToken;
  } catch (error) {
    if (error instanceof InteractionRequiredAuthError) {
      await msalInstance.acquireTokenRedirect({ scopes: ["openid", "profile", "email"], account });
      return null;
    }
    throw error;
  }
}
